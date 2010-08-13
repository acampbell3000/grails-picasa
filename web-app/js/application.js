/*
 * Loading icon for all detected Ajax requests
 */
$(document).ajaxStart(function() {
    $("#loading").show(300);
});
$(document).ajaxStop(function() {
    $("#loading").hide(300);
});

/*
 * Custom ajax fading functions
 */
function displayLoading(div) {
    // Slightly fade selected DIV element
    $(div).fadeTo(500, 0.5)
}
function displayResponse(div) {
    // Restore opactity
    $(div).fadeTo(500, 1.5)
}
