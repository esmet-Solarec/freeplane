/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2016 jberry
 *
 *  This file author is jberry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.link.mindmapmode.editor;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

import org.freeplane.core.resources.components.BooleanProperty;
import org.freeplane.core.resources.components.IPropertyControl;
import org.freeplane.core.resources.components.NumberSliderProperty;
import org.freeplane.features.link.ConnectorModel;
import org.freeplane.features.link.LinkController;
import org.freeplane.features.link.mindmapmode.MLinkController;
import org.freeplane.features.mode.Controller;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class OpacityControlGroup implements ControlGroup {

	private BooleanProperty mSetOpacity;
	private NumberSliderProperty mOpacity;

	private OpacityChangeListener propertyChangeListener;
	
	private ConnectorModel connector;
	
	private class OpacityChangeListener extends ControlGroupChangeListener {
		public OpacityChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final PropertyChangeEvent evt) {
            final MLinkController linkController = (MLinkController) Controller
            .getCurrentModeController().getExtension(
                    LinkController.class);
			try {
			    linkController.setOpacity(connector, enabled ? 
			            Optional.of(Integer.valueOf(mOpacity.getValue())) : Optional.empty());
            }
            catch (NumberFormatException e) {
            }
		}

		@Override
		void updateValue() {
            final LinkController linkController = LinkController.getController();
            final Optional<Integer> opacity = connector.getAlpha();
            final Integer viewOpacity = linkController.getOpacity(connector);
 			mSetOpacity.setValue(opacity.isPresent());
			mOpacity.setValue(viewOpacity.toString());
		}
	}

    @Override
    public void updateValue(ConnectorModel connector) {
        this.connector = connector;
        propertyChangeListener.update();
    }

	@Override
	public void addControlGroup(DefaultFormBuilder formBuilder) {
		mSetOpacity = new BooleanProperty(ControlGroup.SET_RESOURCE);
		mOpacity = new NumberSliderProperty("edit_transparency_label", 20, 255, 20);
		mOpacity.setNameAsLabelAndToolTip();
		propertyChangeListener = new OpacityChangeListener(mSetOpacity, mOpacity);
		mSetOpacity.addPropertyChangeListener(propertyChangeListener);
		mOpacity.addPropertyChangeListener(propertyChangeListener);
		mSetOpacity.appendToForm(formBuilder);
		mOpacity.appendToForm(formBuilder);
	}
}
