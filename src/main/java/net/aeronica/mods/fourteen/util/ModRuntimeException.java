/*
 * Aeronica's mxTune MOD
 * Copyright 2019, Paul Boese a.k.a. Aeronica
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package net.aeronica.mods.fourteen.util;

@SuppressWarnings("unused")
public class ModRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -8749888847279548143L;
    
    public ModRuntimeException() { super(); }

    public ModRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ModRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ModRuntimeException(String message)
    {
        super(message);
    }

    public ModRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
