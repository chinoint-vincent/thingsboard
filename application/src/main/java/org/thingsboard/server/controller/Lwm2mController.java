/**
 * Copyright © 2016-2021 The Thingsboard Authors
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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.SaveDeviceWithCredentialsRequest;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.lwm2m.ServerSecurityConfig;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.Map;

import static org.thingsboard.server.controller.ControllerConstants.DEVICE_WITH_DEVICE_CREDENTIALS_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.IS_BOOTSTRAP_SERVER_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class Lwm2mController extends BaseController {

    @Autowired
    private DeviceController deviceController;

    public static final String IS_BOOTSTRAP_SERVER = "isBootstrapServer";


    @ApiOperation(value = "Get Lwm2m Bootstrap SecurityInfo (getLwm2mBootstrapSecurityInfo)",
            notes = "Get the Lwm2m Bootstrap SecurityInfo object (of the current server) based on the provided isBootstrapServer parameter. If isBootstrapServer == true, get the parameters of the current Bootstrap Server. If isBootstrapServer == false, get the parameters of the current Lwm2m Server. Used for client settings when starting the client in Bootstrap mode. " +
                    TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/lwm2m/deviceProfile/bootstrap/{isBootstrapServer}", method = RequestMethod.GET)
    @ResponseBody
    public ServerSecurityConfig getLwm2mBootstrapSecurityInfo(
            @ApiParam(value = IS_BOOTSTRAP_SERVER_PARAM_DESCRIPTION)
            @PathVariable(IS_BOOTSTRAP_SERVER) boolean bootstrapServer) throws ThingsboardException {
        try {
            return lwM2MServerSecurityInfoRepository.getServerSecurityInfo(bootstrapServer);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(hidden = true, value = "Save device with credentials (Deprecated)")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/lwm2m/device-credentials", method = RequestMethod.POST)
    @ResponseBody
    public Device saveDeviceWithCredentials(@RequestBody Map<Class<?>, Object> deviceWithDeviceCredentials) throws ThingsboardException {
        ObjectMapper mapper = new ObjectMapper();
        Device device = checkNotNull(mapper.convertValue(deviceWithDeviceCredentials.get(Device.class), Device.class));
        DeviceCredentials credentials = checkNotNull(mapper.convertValue(deviceWithDeviceCredentials.get(DeviceCredentials.class), DeviceCredentials.class));
        return deviceController.saveDeviceWithCredentials(new SaveDeviceWithCredentialsRequest(device, credentials));
    }
}
