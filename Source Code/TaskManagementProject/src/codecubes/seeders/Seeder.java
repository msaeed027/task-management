package codecubes.seeders;

/**
 * Created by msaeed on 1/27/2017.
 */
public class Seeder implements SeederContract {

    public static Seeder instance;

    public Seeder() {

    }

    public static Seeder getInstance() {
        if (instance == null) {
            instance = new Seeder();
        }
        return instance;
    }

    public void run() {
        // List all seeders here
        new UserTableSeeder().run();
    }
}
