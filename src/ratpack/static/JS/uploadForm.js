/*
 * Created by Chiaki Chikame on 10/17/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */

var r = new Resumable({
  target:'/upload_handler',
  maxFiles: 1,
  chunkSize: 384 * 1024
});

if(!r.support)
{
    $('div#not-supported').show();
}
else
{
    $('div#normal').show();
}

r.assignBrowse($('#btnSelect'));
r.assignDrop($('#dropArea'));

r.on('fileAdded', function(file){
    console.log('File added (' + file.uniqueIdentifier + ')');
    $('div#progress').append("<div id='" + file.uniqueIdentifier + "'>" + file.fileName + ":0</div>");
    r.upload();
});

r.on('fileSuccess', function(file, message){
    console.log('File success (' + file.uniqueIdentifier + ')');
    $('div#progress > div#' + file.uniqueIdentifier).text(file.fileName + ":" +"Success!");
    setTimeout(function(){
        $('div#progress > div#' + file.uniqueIdentifier).remove();
    }, 2000);
    setTimeout(function(){
        var getMd5 = $.ajax({
                    url: 'digest/' + file.uniqueIdentifier,
                    method: 'GET'
        });
        getMd5.done(function(msg) {
            $('div#md5').append("<div>" + file.fileName + ":" + msg + "</div>");
        });
        getMd5.fail(function(jqXHR, msg) {
            $('div#md5').append("<div>" + file.fileName + ":failed</div>");
        });
    }, 100);
});

r.on('fileProgress', function(file) {
    var prg = file.progress() * 100;
    $('div#progress > div#' + file.uniqueIdentifier).text(file.fileName + ":" + prg.toFixed(2));
});