//Imports
const functions = require('firebase-functions');
const gcs = require('@google-cloud/storage')();
const spawn = require('child-process-promise').spawn;
const path = require('path');
const os = require('os');
const fs = require('fs');
const admin = require('firebase-admin');
const secureCompare = require('secure-compare');
admin.initializeApp(functions.config().firebase);

// Listens for new messages added to messages/:pushId
exports.pushNotification = functions.database.ref('/Initiatives/{pushId}/{sectorId}/').onWrite( event => {

    console.log('Push notification event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.data.val();
  // Create a notification
    const payload = {
        data: {
            "titulo":valueObject.Titulo,
            "tipo": valueObject.Tipo,
            "descripcion": valueObject.Descripcion,
            "latitud": valueObject.Latitud.toString(),
            "longitud": valueObject.Longitud.toString(),
        },
    };

  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    if(valueObject.Estado==1){
      return admin.messaging().sendToTopic(valueObject.Tipo, payload, options);
    }
    return;
    
});

// [START generateThumbnailTrigger]
exports.generateThumbnail = functions.storage.object().onChange(event => {
// [END generateThumbnailTrigger]
  // [START eventAttributes]
  const object = event.data; // The Storage object.

  const fileBucket = object.bucket; // The Storage bucket that contains the file.
  const filePath = object.name; // File path in the bucket.
  const contentType = object.contentType; // File content type.
  const resourceState = object.resourceState; // The resourceState is 'exists' or 'not_exists' (for file/folder deletions).
  const metageneration = object.metageneration; // Number of times metadata has been generated. New objects have a value of 1.
  // [END eventAttributes]

  // [START stopConditions]
  // Exit if this is triggered on a file that is not an image.

  // Get the file name.
  const fileName = path.basename(filePath);
  // Exit if the image is already a thumbnail.
  if (fileName.startsWith('thumb_')) {
    console.log('Already a Thumbnail.');
    return;
  }

  // Exit if this is a move or deletion event.
  if (resourceState === 'not_exists') {
    console.log('This is a deletion event.');
    return;
  }

  // Exit if file exists but is not new and is only being triggered
  // because of a metadata change.
  if (resourceState === 'exists' && metageneration > 1) {
    console.log('This is a metadata change event.');
    return;
  }
  // [END stopConditions]

  // [START thumbnailGeneration]
  // Download file from bucket.
  const bucket = gcs.bucket(fileBucket);
  const tempFilePath = path.join(os.tmpdir(), fileName);
  return bucket.file(filePath).download({
    destination: tempFilePath
  }).then(() => {
    console.log('Image downloaded locally to', tempFilePath);
    // Generate a thumbnail using ImageMagick.
    return spawn('convert', [tempFilePath, '-thumbnail', '300x300>', tempFilePath]);
  }).then(() => {
    console.log('Thumbnail created at', tempFilePath);
    // We add a 'thumb_' prefix to thumbnails file name. That's where we'll upload the thumbnail.
    const thumbFileName = `thumb_${fileName}`;
    const thumbFilePath = path.join(path.dirname(filePath), thumbFileName);
    // Uploading the thumbnail.
    return bucket.upload(tempFilePath, {destination: thumbFilePath});
  // Once the thumbnail has been uploaded delete the local file to free up disk space.
  }).then(() => fs.unlinkSync(tempFilePath));
  // [END thumbnailGeneration]
});
// [END generateThumbnail]



exports.initiativesStateChange = functions.https.onRequest((req, res) => {
  const key = req.query.key;
  if (!secureCompare(key, functions.config().cron.key)) {
    console.log('The key provided in the request does not match the key set in the environment. Check that', key,
        'matches the cron.key attribute in `firebase env:get`');
    res.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
        'cron.key environment variable.');
    return;
  }

  RunRef = admin.database().ref('/Initiatives/');
   return RunRef.once('value').then(snapshot => {
      if (snapshot.hasChildren()) {
        snapshot.forEach(function(child) { 
          if (child.hasChildren()) {
            child.forEach(function(child2) { 

              if(child2.child("Estado").val()==0 && child2.child("fechaInicio").val()<=Date.now()){
                child2.ref.update({
                 "Estado": 1
                 });
              }
              else if(child2.child("Estado").val()==1 && child2.child("fechaFin").val()-600000<=Date.now()){
               child2.ref.update({
                  "Estado": 2
                 });
               }
              else if(child2.child("Estado").val()==2 && child2.child("fechaFin").val()<=Date.now()){
                child2.ref.update({
                  "Estado": 3
                 });
              }
            });
          }

          
        });
      }
      res.end();
      return;
    });
   

});

