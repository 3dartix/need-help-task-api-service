package ru.pugart.task.api.service.config;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import ru.pugart.task.api.service.repository.entity.Category;
import ru.pugart.task.api.service.service.CategoryService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Configuration
@Data
@Slf4j
@RequiredArgsConstructor
public class CategoryConfig {

    private static String SPLITTER = ";";

    @Value("classpath:categories/data.csv")
    private Resource resource;

    private final CategoryService categoryService;

    @SneakyThrows
    @PostConstruct
    public void map(){
        Map<Category, List<Category>> categories = new HashMap<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(resource.getFile()))
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .withSkipLines(1)
                .build()) {

            String[] values;


            while ((values = csvReader.readNext()) != null) {
                Category mainCategory = categoryBuild(values[0], null);
                List<Category> cubCategories = new ArrayList<>();
                Arrays.asList(values[1].split(SPLITTER)).forEach(subCategory -> cubCategories.add(categoryBuild(subCategory, mainCategory)));
                categories.put(mainCategory, cubCategories);
            }

            {
                log.info("saving to elastic... ");
                List<Category> allCategories = new ArrayList<>();
                categories.forEach((k, v) -> {
                    allCategories.add(k);
                    allCategories.addAll(v);
                });
                categoryService.store(allCategories);
            }
            
            log.info("categories: {}", categories);
        }
    }

    private Category categoryBuild(String name, Category mainCategory){
        String transliterateName = transliterate(name);
        return Category.builder()
                .id(mainCategory == null ? transliterateName : mainCategory.getId()  + "_" + transliterateName)
                .categoryMain(mainCategory != null ? mainCategory.getName() : null)
                .name(name)
                .build();
    }

    private String transliterate(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц','ч', 'ш','щ','ъ','ы','ь','э', 'ю','я','А','Б','В','Г','Д','Е','Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч','Ш', 'Щ','Ъ','Ы','Ь','Э','Ю','Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        String[] abcLat = {"_","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","sch", "","i", "","e","ju","ja","A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Sch", "","I", "","E","Ju","Ja","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

//    private static Map<Category, List<Category>> categories = new HashMap<>();

//    public Category getCategoryIfExits(String mainNameCategory, String subNameCategory){
//        log.info("trying find category by mainNameCategory {} and subNameCategory {}", mainNameCategory, subNameCategory);
//        for (Map.Entry<Category, List<Category>> mainCategory : categories.entrySet()) {
//            if(mainCategory.getKey().getName().equalsIgnoreCase(mainNameCategory)){
//
//                if(subNameCategory == null) {
//                    log.info("category found: {}", mainCategory.getKey());
//                    return mainCategory.getKey();
//                }
//
//                for (Category subCategory: mainCategory.getValue()) {
//                    if (subCategory.getName().equalsIgnoreCase(subNameCategory)){
//                        log.info("category found: {}",subCategory);
//                        return subCategory;
//                    }
//                }
//            }
//        }
//        log.warn("category not found");
//        return null;
//    }
}
