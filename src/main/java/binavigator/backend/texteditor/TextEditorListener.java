package binavigator.backend.texteditor;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.*;

public class TextEditorListener implements KeyListener, MouseMotionListener, MouseListener, FocusListener, CaretListener{
	private TextEditorController controller;

	public TextEditorListener (TextEditorController controller){
		this.controller = controller;
	}
	@Override
	public void keyTyped(KeyEvent keyEvent) {

	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		//Paint Current Scope
		if (keyEvent.getKeyCode() == KeyEvent.VK_9 && keyEvent.isControlDown()) {
			System.out.println("Print Here");
			controller.findParentheses();
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_UP ||
				keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
			controller.caretMoved();
		}
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		controller.caretMoved();
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		controller.caretMoved();
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {

	}

	@Override
	public void focusGained(FocusEvent focusEvent) {
		System.out.println("Focus Gained");
	}

	@Override
	public void focusLost(FocusEvent focusEvent) {
		System.out.println("Focus Lost");
	}

	@Override
	public void caretUpdate(CaretEvent caretEvent) {
		controller.caretMoved();
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		controller.caretMoved();
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {

	}
}
