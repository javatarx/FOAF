/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RDFS;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import pe.edu.pucp.generator.PersonGenerator;

/**
 *
 * @author imagen4-upeu
 */
public class GenRDF {
    public static void main(String[] args) {
        String NS = "http://somewhere.com/";
        Model model = ModelFactory.createDefaultModel();
        
        PersonGenerator.generate(model, NS);
        PersonGenerator.generate(model, NS);
        PersonGenerator.generate(model, NS);
        
        model.write(System.out);
    }
}
