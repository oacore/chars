package uk.ac.core.dataprovider.api.annotation.documentation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.ac.core.dataprovider.api.model.ErrorsResponse;
import uk.ac.core.dataprovider.api.model.dataprovider.CompactDataProviderResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiOperation("Adds a new dataprovider")
@ApiResponses({
        @ApiResponse(code = 200, message = "Request was successful", response = CompactDataProviderResponse.class),
        @ApiResponse(code = 400, message = "Validation error(s)", response = ErrorsResponse.class),
        @ApiResponse(code = 405, message = "Method not allowed"),
        @ApiResponse(code = 409, message = "Data provider already exists")
})
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostDocumentation {
}
