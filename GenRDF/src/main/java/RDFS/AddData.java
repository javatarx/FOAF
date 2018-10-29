package RDFS;

import java.lang.Object;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.VCARD;

public class AddData {
    public static void main(String[] args) throws IOException{
        //String inputFileName = "proyecto.rdf";
        Model model = ModelFactory.createDefaultModel();
        //InputStream in = FileManager.get().open(inputFileName);
        //model.read(in, "");
        
        String personURI1 = "http://somewhere.com/PedroPerez";
        String personURI2 = "http://somewhere.com/JuanPerez";
        
        String firstName = "Pedro";
        String lastName = "Perez";
        String age = "26";
        Resource pedroPerez = model.createResource(personURI1,FOAF.Person)
        .addProperty(FOAF.firstName,firstName)
        .addProperty(FOAF.lastName, lastName)
        .addProperty(FOAF.age, age);
        
        
        firstName = "Juan";
        lastName = "Perez";
        age = "20";
        model.createResource(personURI2,FOAF.Person)
        .addProperty(FOAF.firstName,firstName)
        .addProperty(FOAF.lastName, lastName)
        .addProperty(FOAF.age, age)
        .addProperty(FOAF.knows, pedroPerez);
        
        
        String rules = "[MyRule1: (?a rdf:type http://xmlns.com/foaf/0.1/Person)-> (?a rdf:type http://xmlns.com/foaf/0.1/Agent)]"
                + "[MyRule2: (?a http://xmlns.com/foaf/0.1/knows ?b)-> (?b http://xmlns.com/foaf/0.1/knows ?a)]";
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        reasoner.setDerivationLogging(true);
        InfModel inf = ModelFactory.createInfModel(reasoner, model);
        
        
        /*
        String fileName = "proyecto.rdf";
        FileWriter out = new FileWriter( fileName );
        try {
            model.write( out, "RDF/XML-ABBREV" );
        }
        finally {
           try {
               out.close();
           }
           catch (IOException closeException) {
               // ignore
           }
        }
*/
        //model.write(System.out, "TURTLE");
         /*System.out.println("lista de sujetos...");
        ResIterator resIter = model.listSubjects();
        while (resIter.hasNext()) {
            Resource res = resIter.nextResource();
            System.out.println(res.getClass());
        }
*/
        System.out.println("selección simple...");
        //Resource sujeto = model.getResource(personURI);
        Resource sujeto = null;
        Property predicado = FOAF.knows;
        RDFNode objeto = null;
        Selector selector = new SimpleSelector(sujeto, predicado, objeto);
        
        System.out.println("Relacion de conocidos (sin inferencia):");
        StmtIterator iter = model.listStatements(selector);
        while (iter.hasNext()) {
            System.out.println(iter.nextStatement().toString());
        }
        
        System.out.println("Relacion de conocidos (con inferencia):");
        iter = inf.listStatements(selector);
        while (iter.hasNext()) {
            System.out.println(iter.nextStatement().toString());
        }
        
        System.out.println("");
        iter = inf.listStatements();
        while (iter.hasNext()) {
            System.out.println(iter.nextStatement().toString());
        }
    }
}
