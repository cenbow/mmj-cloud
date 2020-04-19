package com.mmj.order.tools;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 拆单
 */
public class PackageParse {

    private List<SkuDepot> skuDepots;

    public PackageParse(List<SkuDepot> skuDepots) {
        this.skuDepots = skuDepots;
    }

    /**
     * 拆单方法
     *
     * @return
     */
    public List<Depot> spinOff() {
        List<Depot> resultDepots = Lists.newArrayList();
        if (skuDepots.size() == 1) {
            SkuDepot skuDepot = skuDepots.get(0);
            resultDepots.add(new Depot(skuDepot.getDepots()[0], skuDepots));
            return resultDepots;
        }
        Stream<SkuDepot> skuDepotStream = skuDepots.stream().sorted(Comparator.comparing(SkuDepot::getWeights));
        skuDepotStream.forEach(skuDepot -> {
            List<String> depots = Arrays.asList(skuDepot.getDepots());
            List<SortDepot> sortDepots = Lists.newArrayListWithCapacity(depots.size());
            for (String depotId : depots) {
                long n = skuDepots.stream().filter(d -> Arrays.asList(d.getDepots()).contains(depotId)).count();
                if (n > 0)
                    sortDepots.add(new SortDepot(depotId, n));
            }
            if (sortDepots.size() > 0) {
                SortDepot sortDepot = sortDepots.stream().sorted(Comparator.comparing(SortDepot::getNum).reversed()).findFirst().get();
                if (sortDepot != null) {
                    List<SkuDepot> tempSkuDepotList = skuDepots.stream().filter(d -> Arrays.asList(d.getDepots()).contains(sortDepot.getDepotId())).collect(Collectors.toList());
                    skuDepots.removeAll(tempSkuDepotList);
                    resultDepots.add(new Depot(sortDepot.getDepotId(), tempSkuDepotList));
                }
            }
        });
        return resultDepots;
    }




    public static void main(String[] args) {
        // 1=ABCDE
        // 2=AF
        // 3=BG
        // 4=H
        // result ABFGH
        // 1=AF 3=BG 4=H
        List<SkuDepot> skuDepots = Lists.newArrayList();
        skuDepots.add(new SkuDepot("MM003", 2, "20181128006", "20181128007"));
        skuDepots.add(new SkuDepot("MM007", 2, "20181128008", "20181128007"));
        skuDepots.add(new SkuDepot("F", 1, "20181128008"));
        skuDepots.add(new SkuDepot("G", 1, "20181128007"));
        skuDepots.add(new SkuDepot("H", 1, "20181128006"));

        PackageParse packageParse = new PackageParse(skuDepots);
        List<Depot> depots = packageParse.spinOff();
        depots.forEach(System.out::println);
    }


}
