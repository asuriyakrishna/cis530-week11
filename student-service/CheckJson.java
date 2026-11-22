import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bellevue.cis530.studentservice.entity.Student;

public class CheckJson {
  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Student s = mapper.readValue("{\"firstName\":\"Nina\",\"lastName\":\"Brown\",\"email\":\"nina.brown@example.com\"}", Student.class);
    System.out.println(s.getFirstName() + "|" + s.getLastName() + "|" + s.getEmail());
  }
}