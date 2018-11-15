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
package eu.rageproject.asset.manager;

import java.util.EnumSet;

/**
 * Interface for logger.
 */
public interface ILog
{
    /**
     * Executes the log operation.
     * 
     * Implement this in Game Engine Code.
     *
     * @param severity	The severity.
     * @param msg	  	The message.
     */
    void Log(Severity severity, String msg);

    /**
     * An enum constant representing the critical option.
     */
    static final EnumSet<Severity> Critical = EnumSet.of(Severity.Critical);

    /**
     * An enum constant representing the error option.
     */
    static final EnumSet<Severity> Error = EnumSet.of(Severity.Critical , Severity.Error);

    /**
     * An enum constant representing the warning option.
     */
    static final EnumSet<Severity> Warn = EnumSet.of(Severity.Critical , Severity.Error,Severity.Warning);

    /**
     * An enum constant representing the information option.
     */
    static final EnumSet<Severity> Info = EnumSet.of(Severity.Critical , Severity.Error,Severity.Warning, Severity.Information);

    /**
     * An enum constant representing all option.
     */
    static final EnumSet<Severity> All = EnumSet.of(Severity.Critical , Severity.Error,Severity.Warning, Severity.Information, Severity.Verbose);

    /**
     * Values that represent log levels
     */
    public enum LogLevel {
        Critical,
        Error,
        Warn,
        Info,
        All
    }
}