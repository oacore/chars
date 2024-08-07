package uk.ac.core.dataprovider.api.annotation.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.ac.core.dataprovider.api.model.dataprovider.CompactDataProviderResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiOperation(value = "Patches an existing dataprovider",
        notes = "This HTTP PATCH method uses an implementation of <a href=https://tools.ietf.org/html/rfc6902#page-3 >JSON PATCH format<a>." +
                "\n Allowed paths: " +
                "<pre>\\oaiPmhEndpoint" +
                "\n \\name" +
                "\n \\description" +
                "\n \\journal" +
                "\n \\countryCode" +
                "\n \\longitude" +
                "\n \\latitude" +
                "\n \\software" +
                "\n \\homepage" +
                "\n \\metadataFormat" +
                "\n \\source</pre>",
        response = CompactDataProviderResponse.class
)
@ApiResponses({
        @ApiResponse(code = 200, message = "Resource was patched successfully"),
        @ApiResponse(code = 404, message = "Patch path value is malformed/incorrect"),
        @ApiResponse(code = 405, message = "Method not allowed")
})
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PatchDocumentation {
}
