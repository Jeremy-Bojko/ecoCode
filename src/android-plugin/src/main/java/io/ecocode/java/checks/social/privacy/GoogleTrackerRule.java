/*
 * ecoCode SonarQube Plugin
 * Copyright (C) 2020-2021 Snapp' - Université de Pau et des Pays de l'Adour
 * mailto: contact@ecocode.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.ecocode.java.checks.social.privacy;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;

/**
 * Check the presence of the import com.google.android.gms.analytics.Tracker
 * Importing the com.google.android.gms.analytics.Tracker class means that 
 * the app sends hits to Google Analytics. It is not necessarily sensitive 
 * information, but it is a first step towards Google Ads and hence this 
 * practice should be discouraged at early stage.
 */
@Rule(key = "SPRI002", name = "ecocodeGooggleTracker")
public class GoogleTrackerRule implements JavaFileScanner {

    private static final String ERROR_MESSAGE = "It is not necessarily sensitive information, but it is a first step towards Google Ads and hence this practice should be discouraged at early stage.";
    private static final String METHOD_OWNER_TYPE = "com.google.android.gms.analytics.Tracker";

    @Override
    public void scanFile(JavaFileScannerContext context) {
        CompilationUnitTree cut = context.getTree();

        for (ImportClauseTree importClauseTree : cut.imports()) {
            ImportTree importTree = null;

            if (importClauseTree.is(Tree.Kind.IMPORT)) {
                importTree = (ImportTree) importClauseTree;
            }

            if (importTree == null) {
                // discard empty statements, which can be part of imports
                continue;
            }

            bluetoothImports.collectBluetoothImport(importTree);
        }
    }

    private void handleResult(JavaFileScannerContext context, BluetoothImports bluetoothImports) {
        if (bluetoothImports.hasBluetoothImports()) {
            if (bluetoothImports.hasBothBluetoothTypeImports()) {
                for (ImportTree importTree : bluetoothImports.getBluetoothLEImports()) {
                    context.reportIssue(this, importTree, GOOD_PRACTICE_MESSAGE);
                }
            } else if (bluetoothImports.hasBluetoothLEImports()) {
                for (ImportTree importTree : bluetoothImports.getBluetoothLEImports()) {
                    context.reportIssue(this, importTree, GOOD_PRACTICE_MESSAGE);
                }
            } else {
                for (ImportTree importTree : bluetoothImports.getBluetoothClassicImports()) {
                    context.reportIssue(this, importTree, ERROR_MESSAGE);
                }
            }
        }
    }
}