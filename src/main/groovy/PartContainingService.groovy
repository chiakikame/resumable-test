import groovy.util.logging.Slf4j
import groovyx.gpars.agent.Agent
import ratpack.server.Service
import ratpack.server.StartEvent

import java.security.MessageDigest

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
class PartContainingService implements Service {
    private Agent<Map<String, TempFile>> parts

    @Override
    void onStart(StartEvent event) throws Exception {
        parts = new Agent([:])
    }

    void addPart(String fileId, int chunkId, int totalChunks, byte[] data) {
        log.info("adding chunk")
        parts << {it.get(fileId, new TempFile(totalChunks)).addChunk(chunkId, data)}
    }

    boolean isFileFinished(String fileId) {
        return parts.val[fileId]?.isFileFinished()
    }

    boolean hasPart(String fileId, int chunkId) {
        return parts.val[fileId]?.hasPart(chunkId)
    }

    String getDigest(String fileId) {
        if(isFileFinished(fileId)) {
            TempFile tf = parts.val[fileId]
            log.info "Removing ${fileId} from temp store"
            parts << {it.remove(fileId)}
            return tf.digestString
        } else {
            null
        }
    }
}

// Chunk will start from 1
@Slf4j
class TempFile {
    private int currentChunk = 1
    private int totalChunkCount
    private Map<Integer, byte[]> chunks = [:]
    private MessageDigest status = MessageDigest.getInstance("MD5")

    TempFile(int expectedChunkCount) {
        totalChunkCount = expectedChunkCount
    }

    boolean isFileFinished() {
        return totalChunkCount < currentChunk
    }

    boolean hasPart(int chunkId) {
        return chunkId < currentChunk || chunkId in chunks
    }

    void addChunk(int chunkId, byte[] data) {

        if (chunkId > totalChunkCount)
            log.warn "Chunk ${chunkId} may not belong to this file: expected # chunks: ${totalChunkCount}"

        if (chunkId != currentChunk) {
            log.info "Chunk ${chunkId} != current chunk (${currentChunk}), holding (total: ${totalChunkCount})"
            chunks[chunkId] = data
        } else {
            log.info "Chunk ${chunkId} == current chunk, processing (total: ${totalChunkCount})"
            status.update(data)
            currentChunk += 1
            eatChunks()
        }
    }

    String getDigestString() {
        return status.digest().encodeHex().toString()
    }

    private void eatChunks() {
        while (currentChunk in chunks && currentChunk <= totalChunkCount) {
            log.info "Sending chunk ${currentChunk} to processor"
            status.update(chunks[currentChunk])
            chunks.remove(currentChunk)
            currentChunk += 1
        }
    }
}