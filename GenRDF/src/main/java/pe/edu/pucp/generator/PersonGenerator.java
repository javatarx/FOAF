/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.pucp.generator;

import com.github.javafaker.Faker;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;

/**
 *
 * @author imagen4-upeu
 */
public class PersonGenerator {

    static Faker faker = new Faker();

    public static Resource generate(Model model, String url) {
        Resource person = model.createResource(url + faker.name().username());
        person.addProperty(FOAF.firstName, faker.name().firstName());

        return person;
    }

}
