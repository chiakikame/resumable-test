/*
 * Created by Chiaki Chikame on 10/16/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */


import groovy.util.logging.Slf4j
import ratpack.server.Service
import groovyx.gpars.agent.Agent
import ratpack.server.StartEvent

@Slf4j
class MD5DictService implements Service {
    Agent<Map<String, String>> knownFiles

    @Override
    void onStart(StartEvent event) throws Exception {
        knownFiles = new Agent([:])
    }

    void storeMd5(String field, String md5Hex) {
        log.info "Adding file ${field} with md5 ${md5Hex}"
        knownFiles << {it[field] = md5Hex}
    }

    String getMd5(String field) {
        knownFiles.val[field]
    }

    boolean hasMd5(String fileId) {
        fileId in knownFiles.val
    }
}
