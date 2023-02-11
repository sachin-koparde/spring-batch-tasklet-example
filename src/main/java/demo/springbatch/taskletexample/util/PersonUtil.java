package demo.springbatch.taskletexample.util;

import demo.springbatch.taskletexample.model.Person;

public class PersonUtil {

    public Person createPerson(String[] data) {
        String personName = data[0];
        int personAge = Integer.parseInt(data[1]);

        return new Person(personName, personAge);

    }

    public String[] personToStringArray(Person person) {

        String[] personArray = new String[2]; //A String Array

        personArray[0] = person.getPersonName();
        personArray[1] = String.valueOf(person.getPersonAge());

        return personArray;

    }

}
