package com.driver.repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Stream;

@Repository
public class AirportRepository {
    HashMap<String, Airport>airportHashMap = new HashMap<>();
    //db for airportName,Airport
    HashMap<Integer, Flight>flightHashMap = new HashMap<>();
    //db for flightId,Flight
    HashMap<Airport, List<Flight>>airportFlightHashMap = new HashMap<>();
    //db for the flights whose fromCity is equal to city of airport
    HashMap<Airport, List<Flight>>incomingFlightHashMap = new HashMap<>();
    //db for the flights whose toCity is equal to city of airport
    HashMap<Integer, List<Integer>>bookedPassengers = new HashMap<>();
    //it holds flightId,list of passengerId of passengers who booked
    HashMap<Integer, List<Integer>> cancelledTickets = new HashMap<>();
    //db to keep track of the cancelled tickets. it has flightid,list<passengerid>
    HashMap<Integer, Passenger>passengerHashMap = new HashMap<>();

    public String addAirport(Airport airport) {
        if (airport == null || airportHashMap.containsKey(airport.getAirportName())) {
            return "FAILURE";
        }
        airportHashMap.put(airport.getAirportName(), airport);
        return "SUCCESS";
    }
    public String getLargestAirportName(){
        Airport largestAirport = null;
        for(Airport airport: airportHashMap.values()){
            if(largestAirport==null || airport.getNoOfTerminals()> largestAirport.getNoOfTerminals() || (airport.getNoOfTerminals() == largestAirport.getNoOfTerminals()
            && airport.getAirportName().compareTo(largestAirport.getAirportName())<0)){
                largestAirport = airport;
            }
        }
        return largestAirport != null ? largestAirport.getAirportName() : null;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){
        double shortestDuration = -1;
        //itereating every flight in the flightHashmap and finding the shortest distance
        for(Flight flight: flightHashMap.values()){
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
                if(shortestDuration==-1 || flight.getDuration()<shortestDuration){
                    shortestDuration= flight.getDuration();
                }
            }
        }
        return shortestDuration;
    }
    public int getNumberOfPeopleOn(Date date, String airportName){
        Airport airport = airportHashMap.get(airportName);
        List<Flight> flights = airportFlightHashMap.getOrDefault(airport, Collections.emptyList());
        List<Flight> incomingFlights = incomingFlightHashMap.getOrDefault(airport, Collections.emptyList());

        // Combine the flights and incoming flights streams and filter for flights on a specific date
        int totalPassengers = Stream.concat(flights.stream(), incomingFlights.stream())
                .filter(f -> f.getFlightDate().equals(date))
                // Map each flight to its maximum capacity and convert to an int stream
                .mapToInt(Flight::getMaxCapacity)
                // Sum up all the maximum capacities to get the total passengers
                .sum();

        return totalPassengers;
    }
    public int calculateFlightFare(int flightId){
        int noOfPeopleWhoHaveAlreadyBooked = bookedPassengers.get(flightId).size();
        return 3000 + noOfPeopleWhoHaveAlreadyBooked * 50;
    }
    public String bookATicket(int flightId, int passengerId){
        Flight flight = flightHashMap.get(flightId);
        if (flight == null) {
            return "FAILURE";
        }
        List<Integer> passengerList = bookedPassengers.computeIfAbsent(flightId, k -> new ArrayList<>());
        for(Integer passenger: passengerList){
            if (passenger == passengerId) {
                return "FAILURE";
            }
        }

        if (passengerList.size() >= flight.getMaxCapacity()) {
            return "FAILURE";
        }

        passengerList.add(passengerId);

        bookedPassengers.put(flightId,passengerList);
        return "SUCCESS";
    }
    public String cancelATicket(int flightId, int passengerId){
        Flight flight = flightHashMap.get(flightId);
        if (flight == null) {
            return "FAILURE";
        }

        List<Integer> passengerIds = bookedPassengers.get(flightId);
        if (passengerIds == null || !passengerIds.contains(passengerId)) {
            return "FAILURE";
        }
        passengerIds.remove((Integer) passengerId);

        //now add all the cancelled tickets to cancelledtickets hashmap
        List<Integer> cancelledPassengers = cancelledTickets.get(flightId);
        if (cancelledPassengers == null) {
            cancelledPassengers = new ArrayList<>();
        }
        cancelledPassengers.add(passengerId);
        cancelledTickets.put(flightId, cancelledPassengers);

        return "SUCCESS";
    }

    public int countOfBookingsDoneByPassengerAllCombined(int passengerId){
        return (int) bookedPassengers.values()
                .stream()
                .filter(passengers -> passengers.contains(passengerId))
                .count();
    }
    public String addFlight(Flight flight){
        //we need to consider following points while adding flights
        //1. add it to flightHashMap
        //2. add it to airportFlightHashMap(which has list of flights)
           //a. add the flights going from the airport
           //b. add the flights coming to the airport
        //3. add it to incomingFlightHashMap.

        //if the flight already added
        if (flight == null || flightHashMap.containsKey(flight.getFlightId())) {
            return "FAILURE";
        }

        //getting the from airport and to airport of the particular flight
        Airport fromAirport = airportHashMap.get(flight.getFromCity().name());
        Airport toAirport = airportHashMap.get(flight.getToCity().name());

        if (fromAirport == null || toAirport == null) {
            return "FAILURE";
        }
        flightHashMap.put(flight.getFlightId(), flight);

        //now add flight to all the lists of flights and update the hashmap
        List<Flight> fromAirportFlights = airportFlightHashMap.computeIfAbsent(fromAirport, k -> new ArrayList<>());
        fromAirportFlights.add(flight);

        List<Flight> toAirportFlights = airportFlightHashMap.computeIfAbsent(toAirport, k -> new ArrayList<>());
        toAirportFlights.add(flight);

        List<Flight> incomingFlights = incomingFlightHashMap.computeIfAbsent(toAirport, k -> new ArrayList<>());
        incomingFlights.add(flight);

        return "SUCCESS";
    }
    public String getAirportNameFromFlightId(int flightId){

        Flight flight = flightHashMap.get(flightId);
        if (flight == null) {
            return null;
        }

        City fromCity = flight.getFromCity();
        if (fromCity == null) {
            return null;
        }

        String cityName = fromCity.name();
        if (cityName == null) {
            return null;
        }

        Airport airport = airportHashMap.get(cityName);
        if (airport == null) {
            return null;
        }

        return airport.getAirportName();
    }
    public int calculateRevenueOfAFlight(int flightId){
        Flight flight = flightHashMap.get(flightId);
        if (flight == null) {
            System.out.println("Invalid Flight ID.");
            return 0;
        }

        List<Integer> passengerIds = bookedPassengers.get(flightId);
        if (passengerIds == null) {
            System.out.println("No passengers have booked for this flight.");
            return 0;
        }

        int noOfPassengers = passengerIds.size();
        int revenue = 3000 + (noOfPassengers * 50);

        List<Integer> cancelledPassengers = cancelledTickets.get(flightId);
        if (cancelledPassengers != null) {
            int noOfCancelledPassengers = cancelledPassengers.size();
            revenue -= (noOfCancelledPassengers * 50);
        }
        return revenue;
    }
    public String addPassenger(Passenger passenger){
        if (passenger==null || passengerHashMap.containsValue(passenger)) {
            return "FAILURE";
        }
        passengerHashMap.put(passenger.getPassengerId(), passenger);
        return "SUCCESS";
    }

}
