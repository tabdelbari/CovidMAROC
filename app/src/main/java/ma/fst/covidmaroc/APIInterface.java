package ma.fst.covidmaroc;

import java.util.List;

import ma.fst.covidmaroc.model.Collision;
import ma.fst.covidmaroc.model.Path;
import ma.fst.covidmaroc.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIInterface {

    @POST("/api/users")
    Call<User> saveUser(@Body User user);
    @POST("/api/paths")
    Call<Path> savePath(@Body Path path);
    @POST("/api/collisions")
    Call<List<Collision>> saveCollisions(@Body List<Collision> collisions);

    @PUT("/api/users/{cin}")
    Call<User> putUser(@retrofit2.http.Path("cin") String cin, @Body User user);

}
