package core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;


public class Parser {

    public Map<String, Object> parse(String html, String url) {
        Map<String, Object> data = new LinkedHashMap<>();
        Document doc = Jsoup.parse(html);

        data.put("url", url);
        data.put("title", doc.title());
        data.put("timestamp", Instant.now().toString());

        Element desc = doc.selectFirst("meta[name=description]");
        data.put("description", desc != null ? desc.attr("content") : "");

        String body = doc.body().text().replaceAll("\\s+", " ").trim();
        data.put("body", body.length() > 2000 ? body.substring(0, 2000) : body);

        // === Content Analysis ===
        String bodyText = doc.body().text();
        data.put("word_count", bodyText.split("\\s+").length);
        data.put("character_count", bodyText.length());
        data.put("estimated_reading_time_minutes", Math.max(1, bodyText.split("\\s+").length / 200));
        data.put("cleaned_text", bodyText.length() > 2000 ? bodyText.substring(0, 2000) : bodyText);
        data.put("html_size_bytes", html.getBytes(StandardCharsets.UTF_8).length);

        // Language detection from <html lang="">
        Element htmlTag = doc.selectFirst("html");
        if (htmlTag != null && htmlTag.hasAttr("lang")) {
            data.put("language", htmlTag.attr("lang"));
        }

        // === SEO & Meta ===
        Map<String, String> metaTags = new HashMap<>();
        for (Element meta : doc.select("meta")) {
            String name = meta.hasAttr("name") ? meta.attr("name") : meta.attr("property");
            if (name != null && !name.isEmpty()) {
                metaTags.put(name, meta.attr("content"));
            }
        }
        data.put("meta", metaTags);

        data.put("meta_description", metaTags.getOrDefault("description", ""));
        data.put("meta_keywords", metaTags.getOrDefault("keywords", ""));
        data.put("meta_author", metaTags.getOrDefault("author", ""));
        data.put("meta_robots", metaTags.getOrDefault("robots", ""));

        // Open Graph
        Map<String, String> ogTags = new HashMap<>();
        metaTags.forEach((k, v) -> { if (k.startsWith("og:")) ogTags.put(k, v); });
        data.put("open_graph", ogTags);

        // === Page Structure ===
        Map<String, List<String>> headings = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) {
            List<String> headingList = doc.select("h" + i).eachText();
            if (!headingList.isEmpty()) headings.put("h" + i, headingList);
        }
        data.put("headings", headings);

        // Images
        List<Map<String, String>> images = new ArrayList<>();
        for (Element img : doc.select("img")) {
            Map<String, String> imgInfo = new HashMap<>();
            imgInfo.put("src", img.absUrl("src"));
            imgInfo.put("alt", img.attr("alt"));
            imgInfo.put("width", img.attr("width"));
            imgInfo.put("height", img.attr("height"));
            images.add(imgInfo);
        }
        data.put("images", images);

        // Forms
        List<Map<String, Object>> forms = new ArrayList<>();
        for (Element form : doc.select("form")) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("action", form.absUrl("action"));
            formData.put("method", form.attr("method"));
            List<String> inputs = new ArrayList<>();
            for (Element input : form.select("input")) {
                inputs.add(input.attr("name"));
            }
            formData.put("inputs", inputs);
            forms.add(formData);
        }
        data.put("forms", forms);

        // Links (categorized)
        List<String> internal = new ArrayList<>();
        List<String> external = new ArrayList<>();
        List<String> email = new ArrayList<>();
        List<String> phone = new ArrayList<>();

        for (Element a : doc.select("a[href]")) {
            String link = a.absUrl("href");
            if (link.startsWith("mailto:")) {
                email.add(link.replace("mailto:", ""));
            } else if (link.startsWith("tel:")) {
                phone.add(link.replace("tel:", ""));
            } else if (link.contains(url)) {
                internal.add(link);
            } else {
                external.add(link);
            }
        }

        data.put("links_internal", internal);
        data.put("links_external", external);
        data.put("emails", email);
        data.put("phones", phone);

        // Canonical URL
        Element canonical = doc.selectFirst("link[rel=canonical]");
        if (canonical != null) data.put("canonical_url", canonical.absUrl("href"));

        // Social media profiles
        List<String> socialLinks = new ArrayList<>();
        for (String domain : Arrays.asList("facebook.com", "twitter.com", "linkedin.com", "instagram.com")) {
            external.stream().filter(link -> link.contains(domain)).forEach(socialLinks::add);
        }
        data.put("social_links", socialLinks);

        // === JSON-LD blocks ===
        List<String> jsonLd = new ArrayList<>();
        for (Element script : doc.select("script[type=application/ld+json]")) {
            jsonLd.add(script.html());
        }
        data.put("schema_org", jsonLd);

        return data;
    }
}
