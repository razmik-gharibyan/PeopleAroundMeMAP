package com.studio29.gharibyan.peoplearoundmemap.repositry.editor

class FollowerProcessing {

    // WRITE FOLLOWERS AS INSTAGRAM TYPE DOES
    fun instagramFollowersType(followers: Long): String? {
        var instaFollowers: String?
        val followersS = followers.toString()
        if (followers >= 10000) {
            instaFollowers = followersS.substring(0, 2) + "." + followersS.substring(2, 3) + "k"
            if (followers >= 100000) {
                instaFollowers = followersS.substring(0, 3) + "k"
                if (followers >= 1000000) {
                    instaFollowers =
                        followersS.substring(0, 1) + "." + followersS.substring(1, 2) + "m"
                    if (followers >= 10000000) {
                        instaFollowers =
                            followersS.substring(0, 2) + "." + followersS.substring(2, 3) + "m"
                        if (followers >= 100000000) {
                            instaFollowers = followersS.substring(0, 3) + "m"
                        }
                    }
                }
            }
        } else {
            instaFollowers = followersS
        }
        return instaFollowers
    }

    // RESIZE USER IMAGE DEPENDING ON AMOUNT OF INSTAGRAM FOLLOWERS
    fun picSizeViaFollower(followers: Long): IntArray {
        val userPicWidthHeight = intArrayOf(100, 100)
        if (followers >= 1000) {
            userPicWidthHeight[0] = 150
            userPicWidthHeight[1] = 150
            if (followers >= 10000) {
                userPicWidthHeight[0] = 200
                userPicWidthHeight[1] = 200
                if (followers >= 50000) {
                    userPicWidthHeight[0] = 250
                    userPicWidthHeight[1] = 250
                    if (followers >= 100000) {
                        userPicWidthHeight[0] = 300
                        userPicWidthHeight[1] = 300
                        if (followers >= 500000) {
                            userPicWidthHeight[0] = 350
                            userPicWidthHeight[1] = 350
                            if (followers >= 1000000) {
                                userPicWidthHeight[0] = 400
                                userPicWidthHeight[1] = 400
                                if (followers >= 15000000) {
                                    userPicWidthHeight[0] = 450
                                    userPicWidthHeight[1] = 450
                                    if (followers >= 50000000) {
                                        userPicWidthHeight[0] = 500
                                        userPicWidthHeight[1] = 500
                                        if (followers >= 100000000) {
                                            userPicWidthHeight[0] = 550
                                            userPicWidthHeight[1] = 550
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return userPicWidthHeight
    }

}