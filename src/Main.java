import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "src/Google Play Store Apps.csv";

        Map<String, Integer> categoryAppsMap = new HashMap<>();
        Map<String, Integer> companyAppsMap = new HashMap<>();
        Map<String, Integer> developersMap = new HashMap<>();
        Map<String, Long> downloadsMap = new HashMap<>();

        getAllMaps(filePath, categoryAppsMap, companyAppsMap, developersMap, downloadsMap);
        writeToFileCategoryApps("CategoryApps.txt", categoryAppsMap);
        writeToFileTopCompanies("Top100Comp.txt", companyAppsMap);
        writeToFileTopDevelopers("Top3Devs.txt", developersMap);


        budgetAppsToBuy(filePath, 1000, "AppsWith1000DollarsBudget.txt");
        budgetAppsToBuy(filePath, 10000, "AppsWith10000DollarsBudget.txt");

        writeToFileFreeVsPaid("FreeVsPaid.txt", downloadsMap);


    }

    private static void writeToFileFreeVsPaid(String file, Map<String, Long> downloadsMap) {

        try {
            FileWriter fileOut = new FileWriter(file);
            fileOut.write("Free/Paid - NumberOfDownloads\n");

            for (Map.Entry<String, Long> entry : downloadsMap.entrySet()) {

                fileOut.write(entry.getKey() + " - " + entry.getValue() + "\n");

            }
            fileOut.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    private static void writeToFileTopDevelopers(String file, Map<String, Integer> developersMap) {


        try{

            FileWriter fileOut = new FileWriter(file);
            fileOut.write("Developer - Number of Apps\n");
            List<Map.Entry<String, Integer>> listOfDevs = new ArrayList<>(developersMap.entrySet());
            listOfDevs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            for(int i=0; i<3; i++){

                fileOut.write(listOfDevs.get(i).getKey() + " - " + listOfDevs.get(i).getValue() +  "\n");

            }

            fileOut.close();


        }catch (Exception e){
            System.out.println(e.getMessage());
        }


    }

    private static void writeToFileTopCompanies(String file, Map<String, Integer> companyAppsMap) {

        try {

            FileWriter fileOut = new FileWriter(file);
            fileOut.write("Company - NumOfApps\n");
            List<Map.Entry<String, Integer>> listOfMaps = new ArrayList<>(companyAppsMap.entrySet());
            listOfMaps.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            for (int i=0; i<100; i++) {
                fileOut.write(listOfMaps.get(i).getKey() + " - " + listOfMaps.get(i).getValue() +  "\n");

            }
            fileOut.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void writeToFileCategoryApps(String file, Map<String, Integer> categoryAppsMap) {

        try {
            FileWriter fileOut = new FileWriter(file);
            fileOut.write("Category - NumberOfApps\n");

            for (Map.Entry<String, Integer> entry : categoryAppsMap.entrySet()) {
                fileOut.write(entry.getKey() + " - " + entry.getValue() + "\n");

            }

            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void getAllMaps(String filepath, Map<String, Integer> categoryAppsMap,
                                   Map<String, Integer> companyAppsMap, Map<String, Integer> developersMap, Map<String, Long> downloadsMap) throws FileNotFoundException {
        File f = new File(filepath);
        Scanner s = new Scanner(f);

        if (s.hasNextLine()) {
            s.nextLine();
        }

        while (s.hasNextLine()) {
            String line = s.nextLine();


            String[] lineParts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            //map1
            String category = lineParts[2].trim();


            categoryAppsMap.put(category, categoryAppsMap.getOrDefault(category, 0) + 1);


            //map2

            String appId = lineParts[1].strip();


            String company = getNameFromId(appId);

            companyAppsMap.put(company, companyAppsMap.getOrDefault(company, 0) + 1);


            //map3


            String wholeEmail = lineParts[15].strip();

            if (!wholeEmail.isEmpty()) {
                String[] emailSplit = wholeEmail.split("@");
                if(emailSplit.length>=2) {
                    String[] afterAtParts = emailSplit[1].strip().toLowerCase().split("\\.");

                    if (afterAtParts.length>=2){
                        String checkCompanyName = afterAtParts[1]+ "." + afterAtParts[0];
                        String companyNameDom = getNameDom(appId);

                        if(!companyNameDom.contains(checkCompanyName)){

                            developersMap.put(wholeEmail, developersMap.getOrDefault(wholeEmail, 0) + 1);

                        }


                    }
                }

            }





            //map4

            try {
                String downloads = lineParts[5].strip();
                if (downloads.startsWith("\"") && downloads.endsWith("\"")) {
                    downloads = downloads.substring(1, downloads.length() - 1);
                }

                downloads = downloads.replace(",", "");
                downloads = downloads.replace(".", "");
                downloads = downloads.replace("+", "");
                boolean isFree = Boolean.parseBoolean(lineParts[8].strip());


                if (isFree) {
                    downloadsMap.put("Free to Download", downloadsMap.getOrDefault("Free to Download", 0L) + Long.parseLong(downloads));
                }   else {
                    downloadsMap.put("Pay to Download", downloadsMap.getOrDefault("Pay to Download", 0L) + Long.parseLong(downloads));
                }


            }catch (Exception e){
                System.out.println(e.getMessage());
            }






        }



        s.close();
    }

    private static String getNameDom(String appId) {
        String[] lineParts = appId.toLowerCase().split("\\.");
        if (lineParts.length >= 2) {
            return lineParts[0] + "." + lineParts[1];
        } else {
            return appId;
        }

    }

    private static String getNameFromId(String appId) {
        String[] lineParts = appId.split("\\.");
        if (lineParts.length >= 2) {
            return lineParts[1];
        } else {
            return appId;
        }
    }



    private static void budgetAppsToBuy(String filepath, double budget, String outputFileName) throws IOException {
        File f = new File(filepath);
        Scanner scanner = new Scanner(f);

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        ArrayList<App> apps = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineParts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if (lineParts[9].contains("M")){
                continue;
            }
            String appName = lineParts[0].trim();

            String priceString = lineParts[9].trim().replaceAll("[^\\d.]", "");


            try {
                double price = Double.parseDouble(priceString);

                apps.add(new App(appName, price));
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }

        }

        scanner.close();


        apps.sort((app1, app2) -> Double.compare(app2.getPrice(), app1.getPrice()));



        int count = 0;
        double rem = budget;

        for (App app : apps) {
            if (rem >= app.getPrice()) {
                rem -= app.getPrice();
                count++;
            }
        }


        try {
            FileWriter fileOut = new FileWriter(outputFileName);
            fileOut.write("Budget - NumberOfApps\n");
            fileOut.write(budget + " - " + count + "\n");
            fileOut.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
