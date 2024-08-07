package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IANATypeList {

    public static ArrayList<String> ianaTypes = new ArrayList<>(Arrays.asList(
            "about",
            "acl",
            "alternate",
            "amphtml",
            "appendix",
            "apple-touch-icon",
            "apple-touch-startup-image",
            "archives",
            "author",
            "blocked-by",
            "bookmark",
            "canonical",
            "chapter",
            "cite-as",
            "collection",
            "contents",
            "convertedfrom",
            "copyright",
            "create-form",
            "current",
            "describedby",
            "describes",
            "disclosure",
            "dns-prefetch",
            "duplicate",
            "edit",
            "edit-form",
            "edit-media",
            "enclosure",
            "external",
            "first",
            "glossary",
            "help",
            "hosts",
            "hub",
            "icon",
            "index",
            "intervalafter",
            "intervalbefore",
            "intervalcontains",
            "intervaldisjoint",
            "intervalduring",
            "intervalequals",
            "intervalfinishedby",
            "intervalfinishes",
            "intervalin",
            "intervalmeets",
            "intervalmetby",
            "intervaloverlappedby",
            "intervaloverlaps",
            "intervalstartedby",
            "intervalstarts",
            "item",
            "last",
            "latest-version",
            "license",
            "linkset",
            "lrdd",
            "manifest",
            "mask-icon",
            "me",
            "media-feed",
            "memento",
            "micropub",
            "modulepreload",
            "monitor",
            "monitor-group",
            "next",
            "next-archive",
            "nofollow",
            "noopener",
            "noreferrer",
            "opener",
            "openid2.local_id",
            "openid2.provider",
            "original",
            "p3pv1",
            "payment",
            "pingback",
            "preconnect",
            "predecessor-version",
            "prefetch",
            "preload",
            "prerender",
            "prev",
            "preview",
            "previous",
            "prev-archive",
            "privacy-policy",
            "profile",
            "publication",
            "related",
            "restconf",
            "replies",
            "ruleinput",
            "search",
            "section",
            "self",
            "service",
            "service-desc",
            "service-doc",
            "service-meta",
            "sip-trunking-capability",
            "sponsored",
            "start",
            "status",
            "stylesheet",
            "subsection",
            "successor-version",
            "sunset",
            "tag",
            "terms-of-service",
            "timegate",
            "timemap",
            "type",
            "ugc",
            "up",
            "version-history",
            "via",
            "webmention",
            "working-copy",
            "working-copy-of"));

    public static boolean contains(String type) {
        return ianaTypes.contains(type.trim());
    }


}