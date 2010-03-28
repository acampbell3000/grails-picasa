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

        "/photo/ajaxList/$albumId" {
            controller = "photo"
            action = "ajaxList"
        }

        "/photo/show/$albumId/$photoId" {
            controller = "photo"
            action = "show"
        }

        "/photo/ajaxShow/$albumId/$photoId" {
            controller = "photo"
            action = "ajaxShow"
        }

        "/"(view:"/index")
        "500"(view:"/error")
    }
}
