package com.company;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static ArrayList<Person> people = new ArrayList<>();
    static final int OFFSET = 20;

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("people.csv");
        Scanner fileReader = new Scanner(f);
        while (fileReader.hasNext()) {
            String line = fileReader.nextLine();
            String[] columns = line.split(",");
            int personID = Integer.parseInt(columns[0]);
            String fName = columns[1];
            String lName = columns[2];
            String em = columns[3];
            String fileCountry = columns[4];
            String fileIP = columns[5];
            Person person = new Person(personID, fName, lName, em, fileCountry, fileIP);
            people.add(person);
        }

        Spark.get(
                "/",
                (request, response) -> {
                    String set = request.queryParams("offset");
                    boolean showBack = false;
                    boolean showNext = true;
                    int next = 0;
                    if (set != null) {
                        next = Integer.valueOf(set);
                    }
                    System.out.println(set);
                    HashMap m = new HashMap();
                    ArrayList<Person> temp = new ArrayList<>(people.subList(next, next + OFFSET));
                    if (next >= 20) {
                        showBack = true;
                    }
                    if (next >= people.size() - OFFSET) {
                        showNext = false;
                    }
                    m.put("temp", temp);
                    m.put("next20", next + OFFSET);
                    m.put("back20", next - OFFSET);
                    m.put("showBack", showBack);
                    m.put("showNext", showNext);

                    return new ModelAndView(m, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                (request, response) -> {
                    //String num = request.queryParams("number");
                    String per = request.queryParams("id");
                    int perID = Integer.parseInt(per);
                    //int pID = Integer.parseInt(num);
                    System.out.println(per);
                    System.out.println(perID);
                    HashMap m = new HashMap();
                    for (Person p : people) {
                        if (p.id == perID) {
                            m.put("person", p);
                            m.put("pID", p.id);
                            m.put("pFN", p.firstName);
                            m.put("pLN", p.lastName);
                            m.put("pEM", p.email);
                            m.put("pCountry", p.country);
                            m.put("pIP", p.ipAddress);
                        }
                    }
//                    for (int i = 0; i < people.size(); i++) {
//                        if (people.get(i).id == pID) {
//                            Person p = people.get(i);
//                            m.put("person", p);
//                            m.put("pID", p.id);
//                            m.put("pFN", p.firstName);
//                            m.put("pLN", p.lastName);
//                            m.put("pEM", p.email);
//                            m.put("pCountry", p.country);
//                            m.put("pIP", p.ipAddress);
//                        }
//                    }
                    return new ModelAndView(m, "/person.html");
                },
                new MustacheTemplateEngine()
        );
    }
}

