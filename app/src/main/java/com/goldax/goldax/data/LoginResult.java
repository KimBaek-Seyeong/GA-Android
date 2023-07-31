package com.goldax.goldax.data;

/**
 * {
 *     "isSuccess": true,
 *     "status": 200,
 *     "result": {
 *         "profile": {
 *             "id": 2,
 *             "email": "aksgur2",
 *             "name": "aksgur2"
 *         },
 *         "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwiZW1haWwiOiJha3NndXIyIiwiaWF0IjoxNTkwMjM4MDQzLCJleHAiOjE2MjYyMzgwNDN9.ahCZ_0S9vFtCbOUZh-Hh_mq4FsaSMhatlN6Uucq3K0w"
 *     }
 * }
 */
public class LoginResult {
    public boolean isSuccess;
    public int status;
    public ResultData result;
    public String description;

    public class ResultData {
        public ProfileData profile;
        public String token;

        public class ProfileData {
            public int id;
            public String email;
            public String name;
        }
    }
}
