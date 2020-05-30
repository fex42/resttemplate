package justme.resttemplate.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import justme.resttemplate.model.Employee
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.URI
import java.util.*

class EmployeeRestClient(private val restTemplate: RestTemplate, host: String, port: Int) {
    private val LOG = LoggerFactory.getLogger(EmployeeRestClient::class.java)
    private val requestUri: String

    /**
     * Requests the employee resource for the specified id via HTTP GET using RestTemplate method getForEntity.
     * @param id the id of the employee resource
     * @return a ResponseEntity that wraps http status code, http headers and the body of type [Employee]
     */
    fun getForEntity(id: Long): ResponseEntity<Employee> {

        // LOG.info("Status code value: " + domain.getStatusCodeValue());
        // LOG.info("HTTP Header 'ContentType': " + domain.getHeaders().getContentType());
        return restTemplate.getForEntity("$requestUri/{id}",
                Employee::class.java,
                id.toString())
    }

    /**
     * Requests a specified amount of employee resources via HTTP GET using RestTemplate method getForEntity.
     * The amount is specified by the given page and pageSize parameter.
     * @param page the page
     * @param pageSize the amount of elements per page
     * @return a list of employees
     */
    fun getAll(page: Int, pageSize: Int): List<Employee> {
        val requestUri = "$requestUri?page={page}&pageSize={pageSize}"
        val urlParameters: MutableMap<String, String?> = HashMap()
        urlParameters["page"] = page.toString()
        urlParameters["pageSize"] = pageSize.toString()
        val entity = restTemplate.getForEntity(requestUri,
                Array<Employee>::class.java,
                urlParameters)
        return if (entity.body != null) listOf(*entity.body!!) else emptyList()
    }

    /**
     * Requests the employee resource for the specified id via HTTP GET using RestTemplate method getForObject.
     * @param id the id of the employee resource
     * @return the employee as [Optional] or an empty [Optional] if resource not found.
     */
    fun getForObject(id: Long): Optional<Employee> {
        val employee = restTemplate.getForObject("$requestUri/{id}",
                Employee::class.java,
                (id))
        return Optional.ofNullable(employee)
    }

    /**
     * Requests the employee resource for the specified id via HTTP GET using RestTemplate method getForObject
     * and returns the resource as JsonNode.
     * @param id the id of the employee resource
     * @return the employee resource as JsonNode
     * @throws IOException if received json string can not be parsed
     */
    @Throws(IOException::class)
    fun getAsJsonNode(id: Long): JsonNode {
        val jsonString = restTemplate.getForObject("$requestUri/{id}",
                String::class.java,
                id)
        val mapper = ObjectMapper()
        return mapper.readTree(jsonString)
    }

    /**
     * Creates an employee resource via HTTP POST using RestTemplate method getForObject.
     * @param employee the employee to be created
     * @return the created employee
     */
    fun postForObject(employee: Employee?): Employee? {
        return restTemplate.postForObject(requestUri, employee, Employee::class.java)
    }

    /**
     * Creates an employee resource via HTTP POST using RestTemplate method getForLocation.
     * @param employee the employee to be created
     * @return the [URI] of the created employee
     */
    fun postForLocation(employee: Employee): URI? {
        return restTemplate.postForLocation(requestUri, HttpEntity(employee))
    }

    /**
     * Creates an employee resource via HTTP POST using RestTemplate method postForEntity.
     * @param newEmployee the employee to be created
     * @return a ResponseEntity that wraps http status code, http headers and the body of type [Employee]
     */
    fun postForEntity(newEmployee: Employee?): ResponseEntity<Employee> {
        val headers: MultiValueMap<String, String> = HttpHeaders()
        headers.add("User-Agent", "EmployeeRestClient demo class")
        headers.add("Accept-Language", "en-US")
        val entity = HttpEntity(newEmployee, headers)
        return restTemplate.postForEntity(requestUri, entity, Employee::class.java)
    }

    /**
     * Updates an employee resource via HTTP PUT using RestTemplate method put.
     * @param updatedEmployee the employee to be updated
     */
    fun put(updatedEmployee: Employee) {
        restTemplate.put("$requestUri/{id}",
                updatedEmployee,
                (updatedEmployee.id))
    }

    /**
     * Updates an employee resource via HTTP PUT using RestTemplate method exchange.
     * @param updatedEmployee the employee to be updated
     * @return a ResponseEntity that wraps http status code, http headers and the body of type [Employee]
     */
    fun putWithExchange(updatedEmployee: Employee): ResponseEntity<Employee> {
        return restTemplate.exchange("$requestUri/{id}",
                HttpMethod.PUT,
                HttpEntity(updatedEmployee),
                Employee::class.java,
                (updatedEmployee.id))
    }

    /**
     * Deletes an employee resurce via HTTP DELETE using RestTemplate method delete.
     * @param id the id of the employee to be deleted
     */
    fun delete(id: Long) {
        restTemplate.delete("$requestUri/{id}", (id))
    }

    /**
     * Deletes an employee resurce via HTTP DELETE using RestTemplate method exchange.
     * @param id the id of the employee to be deleted
     * @return a ResponseEntity that wraps http status code and http headers
     */
    fun deleteWithExchange(id: Long): ResponseEntity<Void> {
        return restTemplate.exchange("$requestUri/{id}",
                HttpMethod.DELETE,
                null,
                Void::class.java,
                (id))
    }

    /**
     * Requests the built request URI via HTTP HEAD.
     * @return the HTTP headers for the requested URI.
     */
    fun headForHeaders(): HttpHeaders {
        return restTemplate.headForHeaders(requestUri)
    }

    /**
     * Requests the built request URI via HTTP OPTION.
     * @param id the employee to be requested with OPTION
     * @return all allowed HTTP methods for the requested URI
     */
    fun optionsForAllow(id: Long): Set<HttpMethod> {
        return restTemplate.optionsForAllow("$requestUri/{id}", (id))
    }

    companion object {
        private const val RESOURCE_PATH = "/rest/employees"
    }

    init {
        requestUri = "$host:$port$RESOURCE_PATH"
    }
}