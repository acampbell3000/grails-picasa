var Ajax;
if (Ajax && (Ajax != null)) {
    Ajax.Responders.register({
        onCreate: function() {
            if($('loader') && Ajax.activeRequestCount > 0)
                Effect.Appear('loader',{
                    duration: 0.3,
					queue:'first'
                });
        },

        onComplete: function() {
            if($('loader') && Ajax.activeRequestCount == 0)
                Effect.Fade('loader',{
                    duration: 0.3,
					queue:'end'
                });
        }
    });
}

/*
 * Custom ajax loading functions
 */
function displayLoading(div) {
    // Slightly fade selected DIV element
    new Effect.Opacity(div, {
        to: 0.5,
        duration: 0.5
    });
}

function displayResponse(div) {
    // Restore opactity
    new Effect.Opacity(div, {
        to: 1.0,
        duration: 0.5
    });
}
