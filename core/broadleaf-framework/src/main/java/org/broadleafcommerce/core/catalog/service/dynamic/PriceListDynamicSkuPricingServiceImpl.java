/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.service.dynamic;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.pricing.domain.PriceAdjustment;
import org.broadleafcommerce.core.pricing.domain.PriceData;
import org.broadleafcommerce.core.pricing.domain.SkuBundleItemPriceData;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 * 
 */
@Service("blPriceListDynamicSkuPricingService")
public class PriceListDynamicSkuPricingServiceImpl implements DynamicSkuPricingService {

    @Override
    public DynamicSkuPrices getSkuPrices(Sku sku,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
        boolean showDefaultSkuPrice=false;
        if(priceList != null && brc.getLocale().getDefaultCurrency().getCurrencyCode().equals("USD")) {
            //show default pricing.
            showDefaultSkuPrice=true;
        }
        
        if (priceList != null && !showDefaultSkuPrice ) {
            if (sku.getPriceDataMap() != null && !sku.getPriceDataMap().isEmpty()) {               
            
                    prices = new DynamicSkuPrices();
                  
                 
                        prices.setRetailPrice(getRetailPriceFromPriceList(sku.getPriceDataMap(), priceList));
                        prices.setSalePrice(getSalePriceFromPriceList( sku.getPriceDataMap(), priceList));
                  
                   
                    if(prices.getRetailPrice()==null && prices.getSalePrice()==null) {
                        prices=new DynamicSkuPrices();
                    }
                
            }
            
            Money adjustments = null;
            if (sku.getProductOptionValueAdjustments() != null) {
                for(ProductOptionValue optionValue : sku.getProductOptionValues()) {
                    if (optionValue.getPriceAdjustmentMap() != null) {                        
                    
                            Money adjustmentAsMoney =getPriceAdjustmentFromPriceList(optionValue.getPriceAdjustmentMap(), priceList);
                        
                            if (adjustments == null) {
                                adjustments = adjustmentAsMoney;
                            } else {
                                adjustments = adjustments.add(adjustmentAsMoney);
                            }
                                
                    }
                }
                prices.setPriceAdjustment(adjustments);
            }           
        } else {
            prices.setRetailPrice(sku.getRetailPrice());
            prices.setSalePrice(sku.getSalePrice());
            prices.setPriceAdjustment(sku.getProductOptionValueAdjustments());
        }
        
        return prices;
    }

    
    private Money getPriceAdjustmentFromPriceList(Map<String, PriceAdjustment> priceAdjustmentMap, PriceList priceList) {
        PriceAdjustment priceData = priceAdjustmentMap.get(priceList.getPriceKey());
        if (priceData == null && priceData.getPriceAdjustment()!=null) {
            return BroadleafCurrencyUtils.getMoney(priceData.getPriceAdjustment(), priceList.getCurrencyCode());
         } else if
         ( Boolean.valueOf(priceList.getUseParentOnNull()) && priceList.getParentPriceList() != null) {
            return getPriceAdjustmentFromPriceList(priceAdjustmentMap, priceList.getParentPriceList());
        }
  
        return null;
    }

    private Money getRetailPriceFromPriceList(Map<String, PriceData> priceDataMap, PriceList priceList) {

        PriceData priceData = priceDataMap.get(priceList.getPriceKey());
       if(priceData!=null && priceData.getRetailPrice()!=null) {
           return BroadleafCurrencyUtils.getMoney(priceData.getRetailPrice(), priceList.getCurrencyCode());
    } else 
        if (Boolean.valueOf(priceList.getUseParentOnNull()) && priceList.getParentPriceList() != null) {

            return getRetailPriceFromPriceList(priceDataMap, priceList.getParentPriceList());
        }

      //  if (priceData == null) {
            return null;
      //  } else {
        

    }

    private Money getSkuBundleSalePriceFromPriceList(Map<String, SkuBundleItemPriceData> priceDataMap, PriceList priceList) {
        SkuBundleItemPriceData priceData = priceDataMap.get(priceList.getPriceKey());
        
        if(priceData != null && priceData.getSalePrice()!=null) {
            return BroadleafCurrencyUtils.getMoney(priceData.getSalePrice(), priceList.getCurrencyCode());
        } 
        else if (Boolean.valueOf(priceList.getUseParentOnNull()) && priceList.getParentPriceList() != null) {
            return getSkuBundleSalePriceFromPriceList(priceDataMap, priceList.getParentPriceList());
        }
        //if (priceData == null) {
            return null;
        
    }

    private Money getSalePriceFromPriceList(Map<String, PriceData> priceDataMap, PriceList priceList) {
        PriceData priceData = priceDataMap.get(priceList.getPriceKey());
        
        if(priceData != null && priceData.getSalePrice()!=null) {
            return BroadleafCurrencyUtils.getMoney(priceData.getSalePrice(), priceList.getCurrencyCode());
        } 
        else if (Boolean.valueOf(priceList.getUseParentOnNull()) && priceList.getParentPriceList() != null) {
            return getSalePriceFromPriceList(priceDataMap, priceList.getParentPriceList());
        }
        //if (priceData == null) {
            return null;
        
    }


    @Override
    public DynamicSkuPrices getSkuBundleItemPrice(
            SkuBundleItem skuBundleItem,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
  
        
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
        boolean showDefaultSkuPrice=false;
        if(priceList != null && brc.getLocale().getDefaultCurrency().getCurrencyCode().equals("USD")) {
            //show default pricing.
            showDefaultSkuPrice=true;
        }
        
        if ((priceList != null) && !showDefaultSkuPrice) {
            if( skuBundleItem.getPriceDataMap() != null  && !skuBundleItem.getPriceDataMap().isEmpty())
              {
                prices.setSalePrice(getSkuBundleSalePriceFromPriceList( skuBundleItem.getPriceDataMap(), priceList));
             }
        }else {
          
            prices.setSalePrice(skuBundleItem.getSalePrice());
          
        }
   
        return prices;     
    }


    @Override
    public DynamicSkuPrices getPriceAdjustment(
            ProductOptionValueImpl skuBundleItem,
            Money priceAdjustment, HashMap skuPricingConsiderationContext) {
        
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
  
        
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
        boolean showDefaultSkuPrice=false;
        if(priceList != null && brc.getLocale().getDefaultCurrency().getCurrencyCode().equals("USD")) {
            //show default pricing.
            showDefaultSkuPrice=true;
        }
        
        
        if ((priceList != null) && !showDefaultSkuPrice) {
            if( skuBundleItem.getPriceAdjustmentMap() != null &&  !skuBundleItem.getPriceAdjustmentMap().isEmpty()) {

                    prices = new DynamicSkuPrices();
                    prices.setPriceAdjustment(getPriceAdjustmentFromPriceList(skuBundleItem
                            .getPriceAdjustmentMap(), priceList));
            }
        } else {
          
            prices.setPriceAdjustment(priceAdjustment);
          
        }
        
        
        return prices;
        
        
    }

    
 
}
