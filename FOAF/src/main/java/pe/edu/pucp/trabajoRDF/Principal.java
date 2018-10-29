package pe.edu.pucp.trabajoRDF;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.sparql.vocabulary.FOAF;

public class Principal {
    public static void main(String[] args) {
        String inputFile="index.rdf";
        Model model=ModelFactory.createDefaultModel();
        InputStream in= FileManager.get().open(inputFile);
        model.read(in,"");
        //model.write(System.out);
        
        StmtIterator iter=model.listStatements();
        /**
         * IMPRESION DE LAS TRIPLAS
         **/
        System.out.println("");
        System.out.println("1. TRIPLAS");
        while(iter.hasNext())
        {
            System.out.println(iter.nextStatement().toString());
        }
        
        /**
         * IMPRESION DE LOS RECURSOS
         **/
        /*CONSULTAS AL RDF*/
        /*System.out.println("");
        System.out.println("RECURSOS");
        ResIterator resIter=model.listSubjects();
        
        while(resIter.hasNext())
        {
            label="";
            comment="";
            Resource res=resIter.nextResource();
            System.out.println(res);
        }*/
        
        /**
         * BUSQUEDA POR PERSON
         **/
        System.out.println("");
        System.out.println("2. BÚSQUEDA DEL RECURSO PERSON");
        
        String personaURI="http://xmlns.com/foaf/0.1/Person";
        Resource persona= model.getResource(personaURI);
        
        Selector selector=new SimpleSelector(persona, null, (RDFNode)null);
        
        iter=model.listStatements(selector);
        while(iter.hasNext())
        {
            System.out.println(iter.nextStatement().toString());
        }
        
        /*Selector selector2=new SimpleSelector(null, null, (RDFNode)persona);
        
        iter=model.listStatements(selector2);
        while(iter.hasNext())
        {
            System.out.println(iter.nextStatement().toString());
        }*/
        
        /**
         * BUSQUEDA DENTRO DE LOS COMENTARIOS
         **/
        System.out.println("");
        System.out.println("3. BÚSQUEDA BÁSICA - EL COMENTARIO HACE REFERENCIA A HOMEPAGE");
        iter=model.listStatements(new SimpleSelector(null, RDFS.comment, (RDFNode)null){
                    @Override
                    public boolean selects(Statement s){
                        return s.getString().contains("homepage");
                    }
                }
        );
        while(iter.hasNext())
        {
            System.out.println(iter.nextStatement().toString());
        }
        
        /**
         * INFERENCIA SIMPLE (STATUS ES SUBPROPIEDAD DE ANNOTATION)
         **/
        System.out.println("");
        System.out.println("4. INFERENCIA SIMPLE (STATUS ES SUBPROPIEDAD DE ANNOTATION)");
        String cuetaOnlineURI="http://xmlns.com/foaf/0.1/OnlineChatAccount";
        Resource cuentaOnline= model.getResource(cuetaOnlineURI);
        
        Property estado =model.getProperty("http://www.w3.org/2003/06/sw-vocab-status/ns#term_status");
        Property anotacion =model.getProperty("http://www.w3.org/2002/07/owl#AnnotationProperty");
        
        System.out.println("------Antes de la inferencia");
        System.out.println("Propiedad status: "+cuentaOnline.getProperty(estado));
        System.out.println("Propiedad annotation: "+cuentaOnline.getProperty(anotacion));
        
        InfModel inf =ModelFactory.createRDFSModel(model);
        
        Resource cuentaOnlineInf=inf.getResource(cuetaOnlineURI);
        System.out.println("------Posterior a la inferencia");
        System.out.println("Propiedad status: "+cuentaOnlineInf.getProperty(estado));
        System.out.println("Propiedad annotation: "+cuentaOnlineInf.getProperty(anotacion));
        
        /**
         * VALIDACION DEL ESQUEMA
         **/
        System.out.println("");
        System.out.println("5. VALIDACIÓN DEL ESQUEMA");
        ValidityReport validity = inf.validate();
        if(validity.isValid())
            System.out.println("El esquema es correcto");
        else{
            System.out.println("Existen conflictos en el esquema");
            for(Iterator i=validity.getReports(); i.hasNext();) {
                System.out.println(" - "+i.next());
            }
        }
        
        /**
         * APLICACION DE REGLAS
         **/
        System.out.println("");
        System.out.println("6. APLICACIÓN DE REGLAS");
        Property name =model.getProperty("http://xmlns.com/foaf/0.1/name");
        
        //String rule="[reglita: (?a ?b ?c) (?d rdfs:subPropertyOf ?b) -> (?a ?d ?c)]";
        String rule="[reglita: (?a rdfs:label ?b) -> (?a http://xmlns.com/foaf/0.1/name ?b)]";
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rule));
        reasoner.setDerivationLogging(true);
        
        InfModel infReglas=ModelFactory.createInfModel(reasoner, model);
        
        PrintWriter out= new PrintWriter(System.out);
        
        for(StmtIterator i=infReglas.listStatements(persona,name,(RDFNode)null); i.hasNext();){
            Statement s=i.nextStatement();
            System.out.println(s);
            for(Iterator id=infReglas.getDerivation(s); id.hasNext();){
                Derivation deriv=(Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();
    }
    
}
