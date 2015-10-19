/*
 * Created by Chiaki Chikame on 9/30/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import ratpack.groovy.template.MarkupTemplateModule

ratpack {
    bindings {
        module MarkupTemplateModule
        def md5 = new MD5DictService()
        def parter = new PartContainingService()
        bindInstance(MD5DictService, md5)
        bindInstance(PartContainingService, parter)
        bindInstance(UploadHandler, new UploadHandler(md5, parter))
    }

    serverConfig {
        port(8080)
    }

    handlers {
        get {
            render groovyMarkupTemplate('index.groovy', 'text/html')
        }

        prefix('upload_handler') {
            all chain(registry.get(UploadHandler))
        }

        get('digest/:fileId') {
            def fileId = pathTokens['fileId']
            def md5 = registry.get(MD5DictService).getMd5(fileId)
            if (md5 == null) {
                clientError(404)
            } else {
                render md5
            }
        }

        fileSystem('static') { f ->
            f.files()
        }
    }
}