import groovy.util.logging.Slf4j
import ratpack.groovy.handling.GroovyChainAction
import ratpack.form.Form
import ratpack.exec.Blocking

/*
 * Created by Chiaki Chikame on 10/17/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */

@Slf4j
class UploadHandler extends GroovyChainAction{
    private MD5DictService md5Store
    private PartContainingService partStore

    UploadHandler(MD5DictService md5, PartContainingService parter) {
        md5Store = md5
        partStore = parter
    }

    @Override
    void execute() throws Exception {
        all {
            byMethod {
                get {
                    String chunkNumberStr = request.queryParams.get('resumableChunkNumber')
                    String uniqueId = request.queryParams.get('resumableIdentifier')
                    String chunkSizeStr = request.queryParams.get('resumableChunkSize')
                    String totalSizeStr = request.queryParams.get('resumableTotalSize')

                    log.info("Testing ${uniqueId}:${chunkNumberStr} (Size: ${chunkSizeStr}, totalSize: ${totalSizeStr})")

                    int chunkNumber = chunkNumberStr.isInteger() ? chunkNumberStr.toInteger() : -1
                    int chunkSize = chunkSizeStr.isInteger() ? chunkSizeStr.toInteger() : -1
                    int totalSize = totalSizeStr.isInteger() ? totalSizeStr.toInteger() : -1

                    if (chunkNumber < -1 || chunkSize < -1 || totalSize < -1) {
                        clientError(400)
                        return
                    }

                    Blocking.get {
                        boolean hasMd5 = md5Store.hasMd5(uniqueId)
                        boolean hasChunk = partStore.hasPart(uniqueId, chunkNumber)
                        return hasMd5 || hasChunk
                    } then { hasMd5OrChunk ->
                        if (hasMd5OrChunk)
                            render ""
                        else
                            clientError(404)
                    }
                }
                post {
                    parse(Form).then({ form ->
                        String chunkNumberStr = form.get('resumableChunkNumber')
                        String uniqueId = form.get('resumableIdentifier')
                        String chunkSizeStr = form.get('resumableChunkSize')
                        String totalSizeStr = form.get('resumableTotalSize')
                        String totalChunksStr = form.get('resumableTotalChunks')
                        byte[] data = form.file('file')?.bytes

                        int chunkNumber = chunkNumberStr.isInteger() ? chunkNumberStr.toInteger() : -1
                        int chunkSize = chunkSizeStr.isInteger() ? chunkSizeStr.toInteger() : -1
                        int totalSize = totalSizeStr.isInteger() ? totalSizeStr.toInteger() : -1
                        int totalChunks= totalChunksStr.isInteger() ? totalChunksStr.toInteger() : -1

                        if (chunkNumber < -1 || chunkSize < -1 || totalSize < -1) {
                            clientError(400)
                            return
                        }

                        log.info("Receiving ${uniqueId}:${chunkNumberStr}/${totalChunks} (Size: ${chunkSizeStr}, totalSize: ${totalSizeStr})")

                        boolean hasMd5OrChunk = false
                        Blocking.get {
                            md5Store.hasMd5(uniqueId) || partStore.hasPart(uniqueId, chunkNumber)
                        } then {
                            hasMd5OrChunk = it
                        }

                        if (hasMd5OrChunk) {
                            render ""
                            return
                        }

                        Blocking.get({
                            partStore.addPart(uniqueId, chunkNumber, totalChunks, data)
                            if (partStore.isFileFinished(uniqueId)) {
                                String digest = partStore.getDigest(uniqueId)
                                md5Store.storeMd5(uniqueId, digest)
                            }
                            return true
                        }).then { ok ->
                            if (!ok) {
                                clientError(500)
                            }
                        }
                        render ""
                    })
                }
            }
        }
    }
}
