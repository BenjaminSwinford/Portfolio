using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class playerDeath : MonoBehaviour
{
    private Animator anim;
    private Rigidbody2D rb;
    private int Health = 100;
    private int currentHealthIndex = 1;

    [SerializeField] private GameObject[] currentHealth;

    [SerializeField] private AudioSource deathSoundEffect;

    private void Start()
    {
        rb = GetComponent<Rigidbody2D>();
        anim = GetComponent<Animator>();

        //CurrentHealth.text = "Health: " + Health;
    }

    private void OnCollisionEnter2D(Collision2D collision)
    {
        if (collision.gameObject.CompareTag("Trap"))
        {
            deathSoundEffect.Play();
            LoseHealth();
            if (Health <= 0)
            {
                Die();
            }
        }
    }

    private void LoseHealth()
    {
        Health -= 49;
        Destroy(currentHealth[currentHealth.Length - currentHealthIndex]);
        currentHealthIndex++;
    }

    private void Die()
    {
        rb.bodyType = RigidbodyType2D.Static;
        anim.SetTrigger("Death");
    }

    private void RestartLevel()
    {
        SceneManager.LoadScene(SceneManager.GetActiveScene().name);
    }
}
