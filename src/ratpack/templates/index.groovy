/*
 * Created by Chiaki Chikame on 10/16/15.
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/txt/copying/ for more details.
 */

layout 'layout.groovy',
bodyContents: contents {
    p {yield "Choose a file and calculate its md5!"}
    includeGroovy '_uploadForm.groovy'
}