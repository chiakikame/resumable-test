/*
 * Created by Chiaki Chikame on 10/16/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */


div(id: 'normal', style: 'display:none') {
    h1 {yield "Progress"}
    div (id:'progress') {}
    h1 {yield "Result"}
    div (id:'md5') {}
    hr()
    a (style: 'color: red', id: 'btnSelect') {yield "Browse"}
    span{yield " from your hard drive, or"}
    br()
    div (style: 'background-color: green; color:yellow; width: 640px; height: 480px; margin: 8px', id: 'dropArea') {
        yield "drop file here from your file manager"
    }
}
div(id: 'not-supported', style: 'display:none') {
    p(style: 'color:red') {yield "Sorry, your system cannot support resumable.js"}
}
script(src: '/JS/resumable.js') {}
script(src: '/JS/uploadForm.js') {}
