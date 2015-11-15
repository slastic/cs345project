import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;

import java.io.FileInputStream;
 

/**
 * Runs queries against a back-end database
 */
public class Query {
    private static Properties configProps = new Properties();

    private static String imdbUrl;
    private static String customerUrl;

    private static String postgreSQLDriver;
    private static String postgreSQLUser;
    private static String postgreSQLPassword;

    // DB Connection
    private Connection _imdb;
    private Connection _customer_db;

    // Canned queries

    private String _search_sql = "SELECT * FROM movie WHERE name like ? ORDER BY id";
    private PreparedStatement _search_statement;

    private String _director_mid_sql = "SELECT y.* "
                     + "FROM movie_directors x, directors y "
                     + "WHERE x.mid = ? and x.did = y.id";
    
    private String _actor_mid_sql = "SELECT a.* "
    				 + "FROM actor a, casts c "
    		         + "WHERE c.mid = ? and a.id = c.pid";
    
    private String _movie_actor_sql = "SELECT a.* , m.id "
			 + "From actor a, movie m, casts c "
			 + "Where a.id = c.pid and c.mid = m.id and m.name like ? ORDER BY m.id";

    private String _movie_director_sql = "SELECT d.*, m.id "
			 + "FROM directors d, movie m, movie_directors md "
			 + "WHERE m.id = md.mid and md.did = d.id and m.name like ? ORDER by m.id";

    private String _rent_sql = "Select * from rentals where MovieId =?";
    
    private String _rent_count_sql = "Select Count(*) from rentals where cid = ?";
    
    private String _plan_sql = "Select plan from person where cid = ?";
    
    private String _plan_details_sql = "Select * from plans where planName = ?";
    				 
    private PreparedStatement _director_mid_statement;
    private PreparedStatement _actor_mid_statement;
    private PreparedStatement _rent_statement;
    private PreparedStatement _rent_count_statement;
    private PreparedStatement _plan_statement;
    private PreparedStatement _plan_details_statement;
    private PreparedStatement _movie_actor_statement;
    private PreparedStatement _movie_director_statement;

    
    private String currentUser;

    private String _customer_login_sql = "SELECT * FROM person WHERE username = ? and password = ?";
    private PreparedStatement _customer_login_statement;

    private String _begin_transaction_read_write_sql = "BEGIN TRANSACTION READ WRITE";
    private PreparedStatement _begin_transaction_read_write_statement;

    private String _commit_transaction_sql = "COMMIT TRANSACTION";
    private PreparedStatement _commit_transaction_statement;

    private String _rollback_transaction_sql = "ROLLBACK TRANSACTION";
    private PreparedStatement _rollback_transaction_statement;

    public Query() {
    }

    /**********************************************************/
    /* Connections to postgres databases */

    public void openConnection() throws Exception {
        configProps.load(new FileInputStream("dbconn.config"));
        
        
        imdbUrl        = configProps.getProperty("imdbUrl");
        customerUrl    = configProps.getProperty("customerUrl");
        postgreSQLDriver   = configProps.getProperty("postgreSQLDriver");
        postgreSQLUser     = configProps.getProperty("postgreSQLUser");
        postgreSQLPassword = configProps.getProperty("postgreSQLPassword");


        /* load jdbc drivers */
        Class.forName(postgreSQLDriver).newInstance();

        /* open connections to TWO databases: imdb and the customer database */
        _imdb = DriverManager.getConnection(imdbUrl, // database
                postgreSQLUser, // user
                postgreSQLPassword); // password

        _customer_db = DriverManager.getConnection(customerUrl, // database
                postgreSQLUser, // user
                postgreSQLPassword); // password
    }

    public void closeConnection() throws Exception {
        _imdb.close();
        _customer_db.close();
    }

    /**********************************************************/
    /* prepare all the SQL statements in this method.
      "preparing" a statement is almost like compiling it.  Note
       that the parameters (with ?) are still not filled in */

    public void prepareStatements() throws Exception {

        _search_statement = _imdb.prepareStatement(_search_sql);
        _director_mid_statement = _imdb.prepareStatement(_director_mid_sql);
        _actor_mid_statement = _imdb.prepareStatement(_actor_mid_sql);
        _rent_statement = _customer_db.prepareStatement(_rent_sql);
        _movie_actor_statement = _imdb.prepareStatement(_movie_actor_sql);
        _movie_director_statement = _imdb.prepareStatement(_movie_director_sql);

        _customer_login_statement = _customer_db.prepareStatement(_customer_login_sql);
        _begin_transaction_read_write_statement = _customer_db.prepareStatement(_begin_transaction_read_write_sql);
        _commit_transaction_statement = _customer_db.prepareStatement(_commit_transaction_sql);
        _rollback_transaction_statement = _customer_db.prepareStatement(_rollback_transaction_sql);

        /* add here more prepare statements for all the other queries you need */
        /* . . . . . . */
        
        _rent_count_statement = _customer_db.prepareStatement(_rent_count_sql);
        _plan_statement = _customer_db.prepareStatement(_plan_sql);
        _plan_details_statement = _customer_db.prepareStatement(_plan_details_sql);
    }


    /**********************************************************/
    /* suggested helper functions  */

    public int helper_compute_remaining_rentals(int cid) throws Exception {
        /* how many movies can she/he still rent ? */
        /* you have to compute and return the difference between the customer's plan
           and the count of oustanding rentals */
        return (99);
    }

    public String helper_compute_customer_name(int cid) throws Exception {
        /* you find  the first + last name of the current customer */
        return ("JoeFirstName" + " " + "JoeLastName");

    }

    public boolean helper_check_plan(int plan_id) throws Exception {
        /* is plan_id a valid plan id ?  you have to figure out */
        return true;
    }

    public boolean helper_check_movie(int mid) throws Exception {
        /* is mid a valid movie id ? you have to figure out  */
        return true;
    }

    private int helper_who_has_this_movie(int mid) throws Exception {
        /* find the customer id (cid) of whoever currently rents the movie mid; return -1 if none */
        return (77);
    }

    /**********************************************************/
    /* login transaction: invoked only once, when the app is started  */
    public int transaction_login(String name, String password) throws Exception {
    	currentUser = name;
        /* authenticates the user, and returns the user id, or -1 if authentication fails */

        int cid;

        _customer_login_statement.clearParameters();
        _customer_login_statement.setString(1,name);
        _customer_login_statement.setString(2,password);
        ResultSet cid_set = _customer_login_statement.executeQuery();
        if (cid_set.next()) 
        	cid = cid_set.getInt(2);
        else 
        	cid = -1;
        return(cid);
    }

    public void transaction_personal_data(int cid) throws Exception {
        /* println the customer's personal data: name, and plan number */
    	/* Find the plan of the current user. */
    	_plan_statement.clearParameters();
    	_plan_statement.setInt(1, cid);
    	ResultSet plan = _plan_statement.executeQuery();
    	_plan_details_statement.clearParameters();
    	
    	/* Find plan details from the given plan name. */
    	if(plan.next())
    		_plan_details_statement.setString(1, plan.getString(1));
    	else
    		_plan_details_statement.setString(1, "valuePlan");
    	
    	/* Get the plan in use's max rental count. */
    	ResultSet details = _plan_details_statement.executeQuery();
    	int rentalsLeft = 0;
    	if(details.next())
    		rentalsLeft = details.getInt(3);
    	
    	/* Get the amount of currently rented movies. */
    	_rent_count_statement.clearParameters();
    	_rent_count_statement.setInt(1, cid);
    	ResultSet count_set = _rent_count_statement.executeQuery();
    	
    	if(count_set.next())
    		rentalsLeft -= count_set.getInt(1);
    	
    	System.out.println(currentUser + ", " + rentalsLeft + " rentals remaining.");
    	plan.close();
    	details.close();
    	count_set.close();
    }


    /**********************************************************/
    /* main functions in this project: */

    public void transaction_search(int cid, String movie_title)
            throws Exception {
        /* searches for movies with matching titles: SELECT * FROM movie WHERE name LIKE movie_title */
        /* prints the movies, directors, actors, and the availability status:
           AVAILABLE, or UNAVAILABLE, or YOU CURRENTLY RENT IT */

        /* set the first (and single) '?' parameter */
        _search_statement.clearParameters();
        _search_statement.setString(1, '%' + movie_title + '%');

        ResultSet movie_set = _search_statement.executeQuery();
        while (movie_set.next()) {
            int mid = movie_set.getInt(1);
            System.out.println("ID: " + mid + " NAME: "
                    + movie_set.getString(2) + " YEAR: "
                    + movie_set.getString(3));
            /* do a dependent join with directors */
            _director_mid_statement.clearParameters();
            _director_mid_statement.setInt(1, mid);
            ResultSet director_set = _director_mid_statement.executeQuery();
            while (director_set.next()) {
                System.out.println("\t\tDirector: " + director_set.getString(3)
                        + " " + director_set.getString(2));
            }

            /* do a dependent join with actors */
            _actor_mid_statement.clearParameters();
            _actor_mid_statement.setInt(1, mid);
            ResultSet actor_set = _actor_mid_statement.executeQuery();
            while (actor_set.next()) {
                System.out.println("\t\tActor: " + actor_set.getString(3)
                        + " " + actor_set.getString(2));
            }

            /* check whether this movie is ready to rent */
            _rent_statement.clearParameters();
            _rent_statement.setInt(1,mid);
            ResultSet rent_set = _rent_statement.executeQuery();
            if(rent_set.next()== false)
                System.out.println("\t\tAVAILABLE");
            else{
            	if(rent_set.getString(1)==currentUser)
            	{
            		System.out.println("\t\tYOU CURRENTLY RENT IT");
            	}
                else
                {
                    System.out.println("\t\tUNAVAILABLE");
                }

            }

            director_set.close();
            actor_set.close();
            rent_set.close();
            /* now you need to retrieve the actors, in the same manner */
            /* then you have to find the status: of "AVAILABLE" "YOU HAVE IT", "UNAVAILABLE" */
        }
        System.out.println();
        movie_set.close();
    }

    public void transaction_choose_plan(int cid, int pid) throws Exception {
        /* updates the customer's plan to pid: UPDATE customers SET plid = pid */
        /* remember to enforce consistency ! */
    }

    public void transaction_list_plans() throws Exception {
        /* println all available plans: SELECT * FROM plan */
    }

    public void transaction_list_user_rentals(int cid) throws Exception {
        /* println all movies rented by the current user*/
    }

    public void transaction_rent(int cid, int mid) throws Exception {
        /* rend the movie mid to the customer cid */
        /* remember to enforce consistency ! */
    }

    public void transaction_return(int cid, int mid) throws Exception {
        /* return the movie mid by the customer cid */
    }

    public void transaction_fast_search(int cid, String movie_title)
            throws Exception {
        /* like transaction_search, but uses joins instead of independent joins
           Needs to run three SQL queries: (a) movies, (b) movies join directors, (c) movies join actors
           Answers are sorted by mid.
           Then merge-joins the three answer sets */

    	// group by hashMap
    	HashMap<String, LinkedList<String>> movieMap = new HashMap<String, LinkedList<String>>();
    	HashMap<String, LinkedList<String>> actorMap = new HashMap<String, LinkedList<String>>();
    	HashMap<String, LinkedList<String>> directorMap = new HashMap<String, LinkedList<String>>();


    	// search the movies that has the similar names.
    	_search_statement.clearParameters();
        _search_statement.setString(1, '%' + movie_title + '%');

        ResultSet movie_set = _search_statement.executeQuery();
        while (movie_set.next())
        {
        	if (!movieMap.containsKey(movie_set.getString(1))){
        		LinkedList<String> cur = new LinkedList<String>();
        		cur.add(movie_set.getString(1));
            	cur.add(movie_set.getString(2));
            	cur.add(movie_set.getString(3));
            	movieMap.put(movie_set.getString(1), cur);   // id as the key

            	// add the key to the actor and director map
            	cur = new LinkedList<String>();
            	actorMap.put(movie_set.getString(1), cur);
            	cur = new LinkedList<String>();
            	directorMap.put(movie_set.getString(1), cur);
        	}
        }
        movie_set.close();


        // search the actors that in this movie

        _movie_actor_statement.clearParameters();
        _movie_actor_statement.setString(1, '%' + movie_title + '%');

        ResultSet actor_set = _movie_actor_statement.executeQuery();

        while (actor_set.next())
        {
        	actorMap.get(actor_set.getString(5)).add(actor_set.getString(2));
        	actorMap.get(actor_set.getString(5)).add(actor_set.getString(3));
        }
        actor_set.close();


        // search the directors that in this movie

        _movie_director_statement.clearParameters();
        _movie_director_statement.setString(1, '%' + movie_title + '%');

        ResultSet director_set = _movie_director_statement.executeQuery();

        while (director_set.next())
        {
        	directorMap.get(director_set.getString(4)).add(director_set.getString(2));
        	directorMap.get(director_set.getString(4)).add(director_set.getString(3));
        }
        director_set.close();

        // To merge the results by the movie, and print it.
        for (String key : movieMap.keySet())
        {
        	System.out.println("Movie ID: "+ movieMap.get(key).get(0) + ", Name: " + movieMap.get(key).get(1) + ", Year: " + movieMap.get(key).get(2));

        	System.out.println("Actors: ");
        	for (int j=0; j<actorMap.get(key).size()/2; j++)
        	{
        		System.out.println("\t\t" + actorMap.get(key).get(j*2) + " " + actorMap.get(key).get(j*2+1));
        	}

        	System.out.println("Directors: ");
        	for (int j=0; j<directorMap.get(key).size()/2; j++)
        	{
        		System.out.println("\t\t" + directorMap.get(key).get(j*2) + " " + directorMap.get(key).get(j*2+1));
        	}
        	System.out.println("----------------------------------------------------------------------------------------");
        }
    }

}
