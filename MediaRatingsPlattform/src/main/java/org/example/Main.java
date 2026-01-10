package org.example;

import org.example.pkgMisc.Config;
import org.example.pkgServer.Server;
import org.example.pkgServer.pkgRepositories.MediaRepository;
import org.example.pkgServer.pkgRepositories.RatingRepository;
import org.example.pkgServer.pkgRepositories.TokenRepository;
import org.example.pkgServer.pkgRepositories.UserRepository;
import org.example.pkgService.LeaderboardService;
import org.example.pkgService.MediaService;
import org.example.pkgService.RatingService;
import org.example.pkgService.UserService;

/*
docker-compose up --build / down -v
docker exec -it postgres psql -U admin -d mediaRatingsDB
\dt
select
*/

public class Main {
    public static void main(String[] args) {
        try  {
            TokenRepository  tokenRepo = new TokenRepository();
            MediaRepository mediaRepo = new MediaRepository();
            UserRepository userRepo = new UserRepository();
            RatingRepository ratingRepo = new RatingRepository();

            LeaderboardService leaderboardService = new LeaderboardService(userRepo, ratingRepo);
            RatingService ratingService = new RatingService(ratingRepo);
            UserService userService = new UserService(userRepo, tokenRepo, ratingRepo, mediaRepo);
            MediaService mediaService = new MediaService(mediaRepo, userRepo, ratingRepo);
            
            Server server = new Server(Integer.parseInt(Config.get("SERVER_PORT")), userService, mediaService, ratingService, leaderboardService);
            server.start();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}