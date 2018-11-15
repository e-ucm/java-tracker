/**
 * Copyright © 2016 e-UCM (http://www.e-ucm.es/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//
// Translated by CS2J (http://www.cs2j.com): 05/11/2018 15:29:16
//

package es.eucm.tracker.Utils;

import es.eucm.tracker.Exceptions.KeyExtensionException;
import es.eucm.tracker.Exceptions.TrackerException;
import es.eucm.tracker.Exceptions.ValueExtensionException;
import es.eucm.tracker.TrackerAsset;
import eu.rageproject.asset.manager.*;

import java.util.ArrayList;
import java.util.List;

public class TrackerAssetUtils   
{
    private TrackerAsset __Tracker;
    TrackerAsset getTracker() {
        return __Tracker;
    }

    void setTracker(TrackerAsset value) {
        __Tracker = value;
    }

    public TrackerAssetUtils(TrackerAsset tracker) {
        this.setTracker(tracker);
    }

    public static List<String> parseCSV(String trace) throws TrackerException {
        ArrayList<String> p = new ArrayList<String>();
        boolean escape = false;
        int start = 0;
        for (int i = 0;i < trace.length();i++)
        {
            char c = trace.charAt(i);
            if (c == '\\')
            {
                escape = true;
            }
            else if (c == ',')
            {
                if (!escape)
                {
                    p.add(trace.substring(start, i).replace("\\,", ","));
                    start = i + 1;
                }
                else
                    escape = false; 
            }
        }
        p.add(trace.substring(start).replace("\\,", ","));
        return p;
    }

    public static boolean quickCheckExtension(String key, Object value) throws Exception {
        return quickCheck(key) && quickCheck(value);
    }

    public boolean checkExtension(String key, Object value) throws Exception {
        return check(key,"Tracker: Extension key is null or empty. Ignored extension.","Tracker: Extension key is null or empty.", KeyExtensionException.class)
                &&
                check(value,"Tracker: Extension value is null or empty. Ignored extension.","Tracker: Extension value is null or empty.", ValueExtensionException.class);
    }

    public static boolean quickCheck(Object value) throws Exception {
        return !(value == null || (value.getClass() == String.class && "".equals(((String)value))) || (value.getClass() == float.class && Float.isNaN((Float)value)));
    }

    public boolean check(Object value, String message, String strict_message, Class<? extends TrackerException> c) throws Exception {
        boolean r = quickCheck(value);
        if (!r)
            notify(message,strict_message, c);
         
        return r;
    }

    public boolean isFloat(String value, String message, String strict_message, RefSupport<Float> result, Class<? extends TrackerException> c) throws TrackerException {
        try{
            result.setValue(Float.parseFloat(value));
        }catch(Exception ex){
            notify(message,strict_message, c);
            return false;
        }
        return true;
    }

    public boolean isBool(String value, String message, String strict_message, RefSupport<Boolean> result, Class<? extends TrackerException> c) throws TrackerException {
        RefSupport<Boolean> res = new RefSupport<Boolean>();
        try{
            result.setValue(Boolean.parseBoolean(value));
        }catch(Exception ex){
            notify(message,strict_message, c);
            return false;
        }
        return true;
    }

    public void notify(String message, String strict_message, Class<? extends TrackerException> c) throws TrackerException {
        if (getTracker().getStrictMode())
        {
            TrackerException ex;
            try {
                ex = c.getConstructor(String.class).newInstance(strict_message);
            } catch (Exception e) {
                getTracker().Log(Severity.Warning, "Class not found");
                ex = new TrackerException(strict_message);
            }

            throw ex;
        }
        else
        {
            getTracker().Log(Severity.Warning, message);
        } 
    }

    public static <T extends Enum<T>> boolean ParseEnum(String text, RefSupport<T> value, Class<T> enumType) throws TrackerException {
        boolean ret = true;
        value.setValue(enumType.getEnumConstants()[0]);
        try
        {
            boolean found = false;
            for (T each : enumType.getEnumConstants()) {
                if (each.name().compareToIgnoreCase(text) == 0) {
                    value.setValue(each);
                    found = true;
                }
            }
            if(!found){
                ret = false;
            }
        }
        catch (Exception e)
        {
            ret = false;
        }

        return ret;
    }

}


