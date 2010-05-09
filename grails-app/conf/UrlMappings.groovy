class UrlMappings {
        static mappings = {
            "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/photo/list/$albumId" {
            controller = "photo"
            action = "list"
        }

        "/photo/list/$albumId/feed/$feed" {
            controller = "photo"
            action = "list"
        }

        "/photo/ajaxList/$albumId" {
            controller = "photo"
            action = "ajaxList"
        }

        "/photo/ajaxList/$albumId/feed/$feed" {
            controller = "photo"
            action = "ajaxList"
        }

        "/photo/show/$albumId/$photoId" {
            controller = "photo"
            action = "show"
        }

        "/photo/show/$albumId/$photoId/feed/$feed" {
            controller = "photo"
            action = "show"
        }

        "/photo/ajaxShow/$albumId/$photoId" {
            controller = "photo"
            action = "ajaxShow"
        }

        "/photo/ajaxShow/$albumId/$photoId/feed/$feed" {
            controller = "photo"
            action = "ajaxShow"
        }

        "/photo/comments/$albumId/$photoId" {
            controller = "photo"
            action = "comments"
        }

        "/photo/comments/$albumId/$photoId/feed/$feed" {
            controller = "photo"
            action = "comments"
        }

        "/"(view:"/index")
        "500"(view:"/error")
    }
}
