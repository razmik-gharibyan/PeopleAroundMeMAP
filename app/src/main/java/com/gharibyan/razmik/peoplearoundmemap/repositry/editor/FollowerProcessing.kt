package com.gharibyan.razmik.peoplearoundmemap.repositry.editor

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
    //TODO String.format
    fun picSizeViaFollower(followers: Long): IntArray {
        val userPicWidthHeight = intArrayOf(110, 150)
        if (followers >= 1000) {
            userPicWidthHeight[0] = 150
            userPicWidthHeight[1] = 230
            if (followers >= 10000) {
                userPicWidthHeight[0] = 200
                userPicWidthHeight[1] = 280
                if (followers >= 50000) {
                    userPicWidthHeight[0] = 250
                    userPicWidthHeight[1] = 330
                    if (followers >= 100000) {
                        userPicWidthHeight[0] = 300
                        userPicWidthHeight[1] = 380
                        if (followers >= 500000) {
                            userPicWidthHeight[0] = 350
                            userPicWidthHeight[1] = 430
                            if (followers >= 1000000) {
                                userPicWidthHeight[0] = 450
                                userPicWidthHeight[1] = 530
                                if (followers >= 15000000) {
                                    userPicWidthHeight[0] = 520
                                    userPicWidthHeight[1] = 600
                                    if (followers >= 50000000) {
                                        userPicWidthHeight[0] = 570
                                        userPicWidthHeight[1] = 650
                                        if (followers >= 100000000) {
                                            userPicWidthHeight[0] = 650
                                            userPicWidthHeight[1] = 730
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