package ma.fst.covidmaroc;

import java.util.List;

import ma.fst.covidmaroc.model.Collision;
import ma.fst.covidmaroc.model.Path;
import ma.fst.covidmaroc.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("/api/collisions")
    Call<Collision> createCollision(@Body Collision collision);

    @GET("/api/users")
    Call<List<User>> getUsers();

    @GET("/api/paths")
    Call<List<Path>> getPaths();

    @GET("/api/collisions")
    Call<List<Collision>> getCollisions();
}
