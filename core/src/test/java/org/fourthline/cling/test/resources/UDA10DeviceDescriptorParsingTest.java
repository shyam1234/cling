/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.test.resources;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.profile.ClientInfo;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.seamless.util.io.IO;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class UDA10DeviceDescriptorParsingTest {

    @Test
    public void readUDA10DescriptorDOM() throws Exception {

        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderImpl();

        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(device, IO.readLines(getClass().getResourceAsStream("/descriptors/device/uda10.xml")));

        SampleDeviceRoot.assertLocalResourcesMatch(
                new MockUpnpService().getConfiguration().getNamespace().getResources(device)
        );
        SampleDeviceRoot.assertMatch(device, SampleData.createRemoteDevice());

    }

    @Test
    public void readUDA10DescriptorSAX() throws Exception {

        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderSAXImpl();

        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(device, IO.readLines(getClass().getResourceAsStream("/descriptors/device/uda10.xml")));

        SampleDeviceRoot.assertLocalResourcesMatch(
                new MockUpnpService().getConfiguration().getNamespace().getResources(device)
        );
        SampleDeviceRoot.assertMatch(device, SampleData.createRemoteDevice());

    }

    @Test
    public void writeUDA10Descriptor() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();
        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderImpl();
        
        RemoteDevice device = SampleData.createRemoteDevice();
        String descriptorXml = binder.generate(
                device,
                new ClientInfo(),
                upnpService.getConfiguration().getNamespace()
        );

/*
        System.out.println("#######################################################################################");
        System.out.println(descriptorXml);
        System.out.println("#######################################################################################");
*/

        RemoteDevice hydratedDevice = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        hydratedDevice = binder.describe(hydratedDevice, descriptorXml);

        SampleDeviceRoot.assertLocalResourcesMatch(
                upnpService.getConfiguration().getNamespace().getResources(hydratedDevice)

        );
        SampleDeviceRoot.assertMatch(hydratedDevice, device);

    }

    @Test
    public void writeUDA10DescriptorWithProvider() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();
        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderImpl();

        LocalDevice device = SampleData.createLocalDevice(true);
        String descriptorXml = binder.generate(
                device,
                new ClientInfo(),
                upnpService.getConfiguration().getNamespace()
        );


        //System.out.println("#######################################################################################");
        //System.out.println(descriptorXml);
        //System.out.println("#######################################################################################");


        RemoteDevice hydratedDevice = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        hydratedDevice = binder.describe(hydratedDevice, descriptorXml);

        SampleDeviceRoot.assertLocalResourcesMatch(
                upnpService.getConfiguration().getNamespace().getResources(hydratedDevice)

        );
        //SampleDeviceRoot.assertMatch(hydratedDevice, device, false);

    }

    @Test
    public void readUDA10DescriptorWithURLBase() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();
        DeviceDescriptorBinder binder = upnpService.getConfiguration().getDeviceDescriptorBinderUDA10();

        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(
                device,
                IO.readLines(getClass().getResourceAsStream("/descriptors/device/uda10_withbase.xml"))
        );

        assertEquals(
                device.normalizeURI(device.getDetails().getManufacturerDetails().getManufacturerURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "mfc.html"
        );
        assertEquals(
                device.normalizeURI(device.getDetails().getModelDetails().getModelURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "someotherbase/MY-DEVICE-123/model.html"
        );
        assertEquals(
                device.normalizeURI(device.getDetails().getPresentationURI()).toString(),
                "http://www.4thline.org/some_ui"
        );

        assertEquals(
                device.normalizeURI(device.getIcons()[0].getUri()).toString(),
                SampleData.getLocalBaseURL().toString() + "someotherbase/MY-DEVICE-123/icon.png"
        );

        assertEquals(device.normalizeURI(
                device.getServices()[0].getDescriptorURI()).toString(),
                     SampleData.getLocalBaseURL().toString() + "someotherbase/MY-DEVICE-123/svc/upnp-org/MY-SERVICE-123/desc.xml"
        );
        assertEquals(
                device.normalizeURI(device.getServices()[0].getControlURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "someotherbase/MY-DEVICE-123/svc/upnp-org/MY-SERVICE-123/control"
        );
        assertEquals(
                device.normalizeURI(device.getServices()[0].getEventSubscriptionURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "someotherbase/MY-DEVICE-123/svc/upnp-org/MY-SERVICE-123/events"
        );

        assertTrue(device.isRoot());
    }

    @Test
    public void readUDA10DescriptorWithURLBase2() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();
        DeviceDescriptorBinder binder = upnpService.getConfiguration().getDeviceDescriptorBinderUDA10();

        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(
                device,
                IO.readLines(getClass().getResourceAsStream("/descriptors/device/uda10_withbase2.xml"))
        );

        assertEquals(
                device.normalizeURI(device.getDetails().getManufacturerDetails().getManufacturerURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "mfc.html"
        );

        assertEquals(
                device.normalizeURI(device.getDetails().getModelDetails().getModelURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "model.html"
        );
        assertEquals(
                device.normalizeURI(device.getDetails().getPresentationURI()).toString(),
                "http://www.4thline.org/some_ui"
        );

        assertEquals(
                device.normalizeURI(device.getIcons()[0].getUri()).toString(),
                SampleData.getLocalBaseURL().toString() + "icon.png"
        );

        assertEquals(device.normalizeURI(
                device.getServices()[0].getDescriptorURI()).toString(),
                     SampleData.getLocalBaseURL().toString() + "svc.xml"
        );
        assertEquals(
                device.normalizeURI(device.getServices()[0].getControlURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "control"
        );
        assertEquals(
                device.normalizeURI(device.getServices()[0].getEventSubscriptionURI()).toString(),
                SampleData.getLocalBaseURL().toString() + "events"
        );

        assertTrue(device.isRoot());
    }

    @Test
    public void readUDA10DescriptorWithEmptyURLBase() throws Exception {
        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderImpl();

        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(device, IO.readLines(getClass().getResourceAsStream("/descriptors/device/uda10_emptybase.xml")));

        SampleDeviceRoot.assertLocalResourcesMatch(
                new MockUpnpService().getConfiguration().getNamespace().getResources(device)
        );
        SampleDeviceRoot.assertMatch(device, SampleData.createRemoteDevice());    }
}

