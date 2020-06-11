package openWeather;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class WeatherForecastOfMajorCities 
{	
	List<Long> allCitiesList =new LinkedList<Long>();
	Long id, MId;
	JSONObject coord;
	Double latitude,longitude;
	Double toatalRainVolume=0.00;
	
	@BeforeTest(enabled = true)
	void getMajorCitiesFromFile() throws IOException, ParseException
	{
		File cityListJson = new File("testData/citylist.json");
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(cityListJson);
		
		JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
		
		for(int i=0;i<=jsonArray.size()-1;i++)
		{
			JSONObject eachItem = (JSONObject) jsonArray.get(i);
			String country = (String)eachItem.get("country");
			String cityName = (String)eachItem.get("name");
			if(country.equals("IN") && (cityName.equals("Mumbai") || cityName.equals("Chennai") || cityName.equals("Chopan") || cityName.equals("Delhi") || cityName.equals("Kolkata") || cityName.equals("Cochin") || cityName.equals("Banglore") || cityName.equals("Cuttack") || cityName.equals("Hyderabad") || cityName.equals("Lucknow")))
			{
				id = (Long)eachItem.get("id");
				allCitiesList.add(id);
				if(cityName.equals("Mumbai"))
				{
						MId = id;
						coord = (JSONObject) eachItem.get("coord");
						latitude = (Double)coord.get("lat");
						longitude = (Double)coord.get("lon");
				}
			}
		}
	}
		
	@Test(enabled = true)
	void getMajorCitiesWithRainHaze()
	{
		System.out.println("1) The major cities in india which are having Rain or Haze:");

		for (int j = 0; j < allCitiesList.size(); j++) 
		{			
			RestAssured.baseURI="https://api.openweathermap.org";
			Response getWeatherOfCity = RestAssured
										.given()
										.queryParam("appid", "9743d5018bfc28f79628d9f852d0f490")
										.queryParam("id", allCitiesList.get(j))
										.get("/data/2.5/weather");
	
			JsonPath jsonResponse = getWeatherOfCity.jsonPath();		
			String cityWeather =jsonResponse.getString("weather[0].main");
		
			if(cityWeather.equals("Rain") || cityWeather.equals("Haze"))
				System.out.println("---> "+jsonResponse.getString("name"));
		}
	}
	
	@Test(enabled = true)
	void getRainVolumeMumbai() throws java.text.ParseException
	{ 	 
		 LocalDate Date1 = LocalDate.now().minusDays(3);
		 LocalDate Date2 = LocalDate.now();
		 String formattedDate1 = Date1.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		 String formattedDate2 = Date2.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		 
		 long startDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
				.parse(formattedDate1+" 00:00:00").getTime() / 1000;
		
		 long endDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
				.parse(formattedDate2+" 00:00:00").getTime() / 1000;
		 
		 RestAssured.baseURI="https://api.openweathermap.org";
		 Response getWeatherOfMumbai = RestAssured
										.given()
										//.log().all()
										.queryParam("appid", "9743d5018bfc28f79628d9f852d0f490")
										.queryParam("lat", latitude)
										.queryParam("lon", longitude)
										.queryParam("start", startDate)
										.queryParam("end", endDate)
										.queryParam("exclude", "current,daily,minutely")
										.get("data/2.5/onecall");
	
			JsonPath jsonResponseOfMumbai = getWeatherOfMumbai.jsonPath();
			List<Object> hourList = jsonResponseOfMumbai.getList("hourly");
			int hourlySize = hourList.size();
			for(int k=0;k<hourlySize;k++)
			{
				
				if(jsonResponseOfMumbai.getString("hourly["+k+"].rain.1h")!=null)
					toatalRainVolume+=jsonResponseOfMumbai.getDouble("hourly["+k+"].rain.1h");
			}
			System.out.println("\n2) The total Volume of rain received from last 3 days: "+toatalRainVolume +" mm\n\n");
	
			 
	}
	
}

