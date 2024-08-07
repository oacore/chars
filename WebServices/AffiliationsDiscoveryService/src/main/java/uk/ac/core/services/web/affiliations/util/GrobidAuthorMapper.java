package uk.ac.core.services.web.affiliations.util;

import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponseItem;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAffiliation;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAuthor;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidOrganization;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class GrobidAuthorMapper implements Function<GrobidAuthor, Stream<AffiliationsDiscoveryResponseItem>> {
    @Override
    public Stream<AffiliationsDiscoveryResponseItem> apply(GrobidAuthor ga) {
        List<AffiliationsDiscoveryResponseItem> items = new ArrayList<>();
        for (GrobidAffiliation gaff : ga.getAffiliations()) {
            AffiliationsDiscoveryResponseItem responseItem = new AffiliationsDiscoveryResponseItem();
            responseItem.setEmail(ga.getEmail());
            responseItem.setAuthor(ga.getFullName());
            GrobidOrganization org = gaff.getOrganizations().stream()
                    .min((o1, o2) -> {
                        if (o1.getType() == o2.getType()) {
                            return o1.getName().compareTo(o2.getName());
                        } else {
                            return o1.getType().compareTo(o2.getType());
                        }
                    })
                    .orElse(null);
            String orgName = org != null ? org.getName() : null;
            responseItem.setInstitution(orgName);
            items.add(responseItem);
        }
        return items.stream();
    }
}
