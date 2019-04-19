import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcEmployee {

   private String JDBC_URL="jdbc:jtds:sybase://192.168.10.21:4901/PRD?ENCRYPT_PASSWORD=true";
   private String JDBC_USERNAME="prd_atomic_user";
   private String JDBC_PASSWORD="A3vt4d28b9";

   private Connection connection;

    public JdbcEmployee() {
        setConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = ClassLoader.getSystemClassLoader().getResourceAsStream("database.properties");
            prop.load(input);

        } catch (IOException io) {
            io.printStackTrace();
        }

        Properties properties=new Properties();
        properties.setProperty("user",JDBC_USERNAME);
        properties.setProperty("password",JDBC_PASSWORD);
        properties.setProperty("useUnicode","yes");
        properties.setProperty("characterEncoding","UTF-8");
        properties.setProperty("serverTimezone","UTC");
        properties.setProperty("autoReconnect","true");
        properties.setProperty("useSSL","false");

        try {
            connection= DriverManager.getConnection(JDBC_URL,properties);
        } catch (Exception e) {
            System.out.println("Error : "+e);
        }
    }

    public void getEmployee(){
        try {
            String sql="select * from employee";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            ResultSet resultSet=preparedStatement.executeQuery();

            JsonArray jsonArray=new JsonArray();

            while (resultSet.next()){
                int id=resultSet.getInt("emp_id");
                String name=resultSet.getString("emp_name");

                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id",id);
                jsonObject.addProperty("name",name);

                jsonArray.add(jsonObject);
            }

            System.out.println("array : "+jsonArray);

        } catch (Exception e) {
            System.out.println("Error : "+e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println("Error : "+e);
            }
        }

    }

    public void deleteEmployee(int id){
        try {
            String sql="delete from employee where emp_id=?";
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setObject(1,id);
           int i=preparedStatement.executeUpdate();
            System.out.println(i);


        } catch (Exception e) {
            System.out.println("Error : "+e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println("Error : "+e);
            }
        }

    }

    public static void main(String[] args) {
        //JdbcEmployee jdbcEmployee=new JdbcEmployee();
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            try {
                Connection con=DriverManager.getConnection(
                        "jdbc:jtds:sybase://192.168.10.21:4901/PRD?LITERAL_PARAMS=true&PACKETSIZE=512&ENCRYPT_PASSWORD=true","prd_atomic_user","A3vt4d28b9");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //jdbcEmployee.getUser();


    }

    private void getUser() {
    }
}
