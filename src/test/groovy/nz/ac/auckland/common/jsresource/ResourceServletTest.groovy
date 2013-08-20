package nz.ac.auckland.common.jsresource

import nz.ac.auckland.util.JacksonHelper
import org.junit.Before
import org.junit.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Author: Marnix
 *
 * Unit test for resource servlet
 */
class ResourceServletTest {

    // ------------------------------------------------------------
    //          Test situations
    // ------------------------------------------------------------

    private List mixedResources = [
            resource(ResourceScope.Global, [ g1: 'global1']),
            resource(ResourceScope.Global, [ g2: 'global2']),
            resource(ResourceScope.Session, [ s1: 'session1'])
    ]

    private List noResources = []

    private List nullResources = null

    private List globalResources = [
            resource(ResourceScope.Global, [ g1: 'global1']),
            resource(ResourceScope.Global, [ g2: 'global2'])
    ]

    private List sessionResources = [
            resource(ResourceScope.Session, [ s1: 'session1'])
    ]

    String pathInfo = "";
    boolean headersSet = false
    StringWriter writer = new StringWriter();
    Map<String, String> headers = [:];

    ResourceServlet servlet;

    @Before
    public void initServlet() {
      servlet = new ResourceServlet();
      servlet.jacksonHelperApi = new JacksonHelper();
    }

    // ------------------------------------------------------------
    //          Tests
    // ------------------------------------------------------------

    @Test
    public void shouldProperlyDetermineResourceScope() {


        assert ResourceScope.Global == servlet.getResourceScopeForPath("/global.js");
        assert ResourceScope.Session == servlet.getResourceScopeForPath("/session.js");
        assert ResourceScope.Unknown == servlet.getResourceScopeForPath("unknown");
        assert ResourceScope.Unknown == servlet.getResourceScopeForPath(null);

    }

    @Test
    public void shouldWriteResourceProperly() {
        StringWriter writer = new StringWriter();

        servlet.writeResource(
                writer, [
                    getResourceMap : { ->
                        return [ key1: 10, key2: 20, key3: "string" ];
                    }
                ] as ApplicationResource
            );

        assert writer.toString() ==
                "UOA.key1 = 10;\n" +
                "UOA.key2 = 20;\n" +
                "UOA.key3 = \"string\";\n";

    }

    @Test
    public void shouldWriteNothingWhenNoMap() {
        StringWriter writer = new StringWriter();

        // no map from resource?
        servlet.writeResource(
                writer, [
                    getResourceMap : { ->
                        return null
                    }
                ] as ApplicationResource
        );

        assert writer.toString() == ""

        // no resource?
        servlet.writeResource(writer, null);
        assert writer.toString() == ""

    }

    /**
     * Test the doGet method
     */
    @Test
    public void shouldTestDoGet() {


        // fake request
        HttpServletRequest request = [
            getPathInfo : { -> return pathInfo}
        ] as HttpServletRequest;

        // fake response
        HttpServletResponse response = [
            setHeader : { String name, String value ->
                headers[name] = value;
            }
        ] as HttpServletResponse;

        // isolated servlet implementation
        ResourceServlet resourceServlet = new ResourceServlet() {
          @Override
          protected Writer getResponseWriter(HttpServletResponse resp) throws IOException {
            return writer;
          }
        }

        resourceServlet.applicationResources = null
        resourceServlet.jacksonHelperApi = new JacksonHelper()

        // test with no writer
        reset();
        writer = null

        try {
            resourceServlet.doGet(request, response);
            assert false;
        }
        catch (IllegalStateException isEx) {
            assert headers['Content-Type'] == "text/javascript"
            assert true;
        }


        // test with unknown scope
        reset();
        pathInfo = "unknown";
        resourceServlet.globalResults = null;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert !headers['Cache-Control']
        assert !headers['Pragma']
        assert writer.toString().indexOf("unable to render") > 0

        // test without any application bundles
        reset();
        pathInfo = "/global.js"
        resourceServlet.applicationResources = noResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("public")
        assert !headers['Pragma']
        assert writer.toString().indexOf("!window.UOA") > 0

        // test without any application bundles
        reset();
        pathInfo = "/global.js"
        resourceServlet.globalResults = null;
        resourceServlet.applicationResources = nullResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("public")
        assert !headers['Pragma']
        assert writer.toString().indexOf("!window.UOA") > 0

        // test global without any global
        reset();
        pathInfo = "/global.js"
        resourceServlet.globalResults = null;
        resourceServlet.applicationResources = sessionResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("public")
        assert !headers['Pragma']
        assert writer.toString().indexOf("!window.UOA") > 0
        assert writer.toString().indexOf("session1") == -1

        // test global without any global
        reset();
        pathInfo = "/session.js"
        resourceServlet.globalResults = null;
        resourceServlet.applicationResources = sessionResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("no-cache")
        assert headers['Pragma'].contains("no-cache");
        assert writer.toString().indexOf("!window.UOA") > 0
        assert writer.toString().indexOf("session1") > 0

        // test global without any global
        reset();
        pathInfo = "/global.js"
        resourceServlet.globalResults = null;
        resourceServlet.applicationResources = mixedResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("public")
        assert !headers['Pragma']
        assert writer.toString().indexOf("!window.UOA") > 0
        assert writer.toString().indexOf("session1") == -1
        assert writer.toString().indexOf("global1") > 0
        assert writer.toString().indexOf("global2") > 0

        // test global without any global
        reset();
        pathInfo = "/session.js"
        resourceServlet.globalResults = null;
        resourceServlet.applicationResources = mixedResources;
        resourceServlet.doGet(request, response);
        assert headers['Content-Type'] == "text/javascript"
        assert headers['Cache-Control'].contains("no-cache")
        assert writer.toString().indexOf("!window.UOA") > 0
        assert writer.toString().indexOf("session1") > 0
        assert writer.toString().indexOf("global1") == -1
        assert writer.toString().indexOf("global2") == -1

    }


    // ------------------------------------------------------------
    //     Convenience methods
    // ------------------------------------------------------------

    /**
     * Reset
     */
    private void reset() {
        pathInfo = "";
        headersSet = false
        writer = new StringWriter();
        headers = [:];
    }

    /**
     * Instanciate a new resource
     */
    private ApplicationResource resource(ResourceScope scope, Map<String, Object> output) {
        return [
                getResourceMap: { -> output },
                getResourceScope : { -> scope },
                getBundleName : { -> bundle }
        ] as ApplicationResource;
    }

}
