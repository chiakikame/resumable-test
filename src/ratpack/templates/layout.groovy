
/*
 * Created by Chiaki Chikame on 10/16/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */

yieldUnescaped '<!DOCTYPE html>'
html {
    head {
        meta(charset:'utf-8')
        title('upload test')
        meta('http-equiv': "Content-Type", content:"text/html; charset=utf-8")
        script(src: '/JS/jquery-2.1.4.min.js') {}
    }
    body {
        div(class:'container') {
            bodyContents()
        }
    }
}