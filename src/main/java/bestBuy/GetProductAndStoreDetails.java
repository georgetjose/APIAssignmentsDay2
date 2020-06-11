package bestBuy;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetProductAndStoreDetails 
{
	String apiKey = "qUh3qMK14GdwAs9bO59QRSCJ";
	@Test(enabled = true)
	void GetStoreDetailsNearPostalCode() 
	{
		RestAssured.baseURI = "https://api.bestbuy.com";
		
		Response getStoreResponse = RestAssured
									.given()
									.queryParam("apiKey", apiKey)
									.queryParam("format", "json")
									.queryParam("show", "name,address,distance")
									.queryParam("pageSize", "1")
									.get("/v1/stores(area(02864,10))");
		//getStoreResponse.prettyPrint();
		JsonPath getStoreResponseJson = getStoreResponse.jsonPath();
		System.out.println("1) The store name, address and distance near to postal code 02864 is below:\n"+getStoreResponseJson.get("stores"));
	}
	
	@Test(enabled = true)
	void canonProducts() 
	{
		RestAssured.baseURI = "https://api.bestbuy.com";
		
		Response getCanonResponse = RestAssured
									.given()
									.queryParam("apiKey", apiKey)
									.queryParam("format", "json")
									.queryParam("show", "name,salePrice")
									.get("/v1/products(manufacturer=canon&salePrice>1000&salePrice<1500)");
		
		JsonPath getCanonResponseJson = getCanonResponse.jsonPath();
		List<Object> getProductsObject = getCanonResponseJson.getList("products");
		
		System.out.println("2) All the canon products of price range between $1000-$1500 \n");
		for(int i=0;i<getProductsObject.size();i++)
		{
			String getProductName =getCanonResponseJson.getString("products["+i+"].name");
			String getProductPrice =getCanonResponseJson.getString("products["+i+"].salePrice");
			System.out.println("Product Name: "+getProductName);
			System.out.println("Product Price: "+getProductPrice+"\n");
		}
			
	}
	
	@Test(enabled = true)
	void iPhone11Pro() 
	{
		RestAssured.baseURI = "https://api.bestbuy.com";
		
		Response getIPhoneResponse = RestAssured
									.given()
									.queryParam("apiKey", apiKey)
									.queryParam("format", "json")
									.queryParam("show", "sku,name,salePrice,regularPrice")
									.get("/v1/products(manufacturer=Apple & productTemplate=Cell_Phones & name=Apple - iPhone 11 Pro* & sku=6341306)");
		
		JsonPath getIphoneResponseJson = getIPhoneResponse.jsonPath();
		
		System.out.println("3) The regular and selling price for iPhone 11 Pro \n");
	
		String getSellingPrice =getIphoneResponseJson.getString("products[0].salePrice");
		String getRegularPrice =getIphoneResponseJson.getString("products[0].regularPrice");
		System.out.println("Sale Price: "+getSellingPrice);
		System.out.println("Regular Price: "+getRegularPrice+"\n");
			
	}
	
	@Test(enabled = true)
	void storeFinderWithPickup() 
	{
		RestAssured.baseURI = "https://api.bestbuy.com";
		
		Response getStoreWithPickupResponse = RestAssured
									.given()
									.queryParam("apiKey", apiKey)
									.queryParam("format", "json")
									.queryParam("show", "stores.storeId,stores.name,stores.address")
									.get("/v1/products(manufacturer=apple & name= Apple - iPhone 11* & inStorePickup=true)+stores(region=RI)");
	
		JsonPath getStoreWithPickupResponseJson = getStoreWithPickupResponse.jsonPath();
		List<Object> getProductsObject = getStoreWithPickupResponseJson.getList("products");
		
		
		Set<String> allStores= new HashSet<String>();
		for(int j=0;j<getProductsObject.size();j++)
		{
			String storeName  =getStoreWithPickupResponseJson.getString("products["+j+"].stores[0].name");
			allStores.add(storeName);
		}
		
		System.out.println("4) The stores having store pick-up availability of iPhone 11 items in stores in RI region \n");
		System.out.println(allStores+"\n");	
	}
}
