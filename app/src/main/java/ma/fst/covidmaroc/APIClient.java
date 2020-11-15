package ma.fst.covidmaroc;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    static Retrofit getClient() {


        return new Retrofit.Builder()
                .baseUrl("https://covid19tracke.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
