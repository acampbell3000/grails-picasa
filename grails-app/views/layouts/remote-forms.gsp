<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title><g:layoutTitle default="remote-forms" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="name" content="remote-forms" />
        <meta name="author" content="Anthony Campbell (anthonycampbell.co.uk)" />
        <meta name="copyright" content="Anthony Campbell (anthonycampbell.co.uk)" />
        <link rel="stylesheet" href="${resource(dir:'css', file:'remote-forms.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images/icon', file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="prototype" />
        <g:javascript library="scriptaculous" />
        <g:javascript library="application" />				
    </head>
    <body>
        <div id="header">
            <div id="loader" style="display: none;">
                <img src="${resource(dir:'images/icon', file:'loader.gif')}" width="32" height="32" alt="${message(code: 'loading.label', default: 'Loading...')}" title="${message(code: 'loading.label', default: 'Loading...')}" />
            </div>
            <div id="logo">
                <a href="http://www.grails.org/plugin/picasa"><img src="${resource(dir:'images', file:'grails-picasa-plugin-logo.png')}" width="163" height="43" alt="Grails Picasa plug-in" title="Grails Picasa plug-in" /></a>
            </div>
        </div>
        <div id="content">
        <g:layoutBody />
        </div>
        <div id="footer">
            <p><a href="http://grails.org/plugin/skin-loader">remote-forms skin</a> by <a href="http://anthonycampbell.co.uk">@acampbell3000</a></p>
        </div>
    </body>
</html>
