import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static class Problem {
        long id;
        String title;
        String url;
    
        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
        
            Problem problem = (Problem) o;
    
            return id == problem.id;
        }
    
        @Override public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    
        @Override public String toString() {
            return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
        }
    }
    
    class OutputRecord {
        Problem problem;
        List<String> companies = new ArrayList<>();
    }
    
    public static void main(String[] args) {
        Map<Problem, List<String>> problemsToCompaniesMap = new HashMap<>();
        File directory = new File("data/csv");
        File[] files = directory.listFiles();
        for (File file : files) {
            FileReader filereader = null;
            try {
                filereader = new FileReader(file);
            } catch (Exception e) {
                System.out.println("Couldn't open file: " + file.getAbsolutePath());
                continue;
            }
    
            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
    
            try {
                nextRecord = csvReader.readNext();
            } catch (Exception e) {
                System.out.println("Couldn't read next line in file: " + file.getAbsolutePath());
                continue;
            }
    
            while (nextRecord != null) {
                try {
                    nextRecord = csvReader.readNext();
                    if (nextRecord == null) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Couldn't read first line in file: " + file.getAbsolutePath());
                    continue;
                }
                Problem problem = new Problem();
                try {
                    problem.id = Long.parseLong(nextRecord[0]);
                } catch (Exception e) {
                    System.out.println("Exception parsing id: " + nextRecord[0] + " in file: " + file.getAbsolutePath());
                    continue;
                }
                problem.title = nextRecord[1];
                problem.url = nextRecord[2];
//                System.out.println(problem);
                String companyName = file.getName();
                problemsToCompaniesMap.computeIfAbsent(problem, p -> new ArrayList<>()).add(companyName);
            }
        }
    
        final List<Map.Entry<Problem, List<String>>> problemsSortedByCompaniesCount =
            problemsToCompaniesMap.entrySet().stream().sorted((o1, o2) -> o2.getValue().size() - o1.getValue().size())
                .collect(Collectors.toList());
        
        
        for(Map.Entry<Problem, List<String>> problemEntry : problemsSortedByCompaniesCount) {
            System.out.println(problemEntry.getKey() + " " + problemEntry.getValue());
        }
    
        try {
            FileWriter writer = new FileWriter("README.md");
            for (Map.Entry<Problem, List<String>> problemEntry : problemsSortedByCompaniesCount) {
                writer.write(line + System.lineSeparator());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
    
    }
}