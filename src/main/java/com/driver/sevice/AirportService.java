package com.driver.sevice;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import com.driver.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
public class AirportService {
    @Autowired
    private AirportRepository airportRepository;
    public void addAirport(Airport airport){

        airportRepository.addAirport(airport);
    }
    public String getLargestAirportName(){

        return airportRepository.getLargestAirportName();
    }
    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){

        return airportRepository.getShortestDurationOfPossibleBetweenTwoCities(fromCity, toCity);
    }
    public int getNumberOfPeopleOn(Date date, String airportName){

        return airportRepository.getNumberOfPeopleOn(date, airportName);
    }
    public int calculateFlightFare(int flightId){
      return airportRepository.calculateFlightFare(flightId);
    }
    public String bookATicket(int flightId, int passengerId){

        return airportRepository.bookATicket(flightId, passengerId);
    }
    public String cancelATicket(int flightId, int passengerId){
        return airportRepository.cancelATicket(flightId, passengerId);
    }
    public int countOfBookingsDoneByPassengerAllCombined(int passengerId){
        return airportRepository.countOfBookingsDoneByPassengerAllCombined(passengerId);
    }
    public String addFlight(Flight flight){
        return airportRepository.addFlight(flight);
    }
    public String getAirportNameFromFlightId(int flightId){
       return airportRepository.getAirportNameFromFlightId(flightId);
    }
    public int calculateRevenueOfAFlight(int flightId){
        return airportRepository.calculateRevenueOfAFlight(flightId);
    }
    public String addPassenger(Passenger passenger){
        return airportRepository.addPassenger(passenger);
    }
}
