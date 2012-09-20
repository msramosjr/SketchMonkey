/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.jme.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DialogExample
{

    public static void main(String[] args) throws Exception
    {

        final JFrame jf = new JFrame("AWT/Swing Dialog Test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("show dialog");
        final Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g)
            {
                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        canvas.setPreferredSize(new Dimension(200,200));

        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JDialog dlg = new JDialog(jf,"modal");
                dlg.add(new JLabel("hello"));
                dlg.setModal(true);
                dlg.pack();
                dlg.setLocationRelativeTo(canvas);
                dlg.setVisible(true);
            }            
        });
        button.setText("press me");
        button.setFocusable(false);
        JPanel panel = new JPanel();
        panel.setBackground(Color.GREEN);        
        panel.setPreferredSize(new Dimension(200, 200));
        jf.setLayout(new BorderLayout());
        panel.add(button);
        jf.add(panel, BorderLayout.NORTH);
        jf.add(canvas, BorderLayout.SOUTH);
        jf.setSize(new Dimension(400,400));
        jf.setVisible(true);
    }    
}